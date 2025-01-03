package com.stockfetcher.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stockfetcher.dto.request.WatchlistRequestDto;
import com.stockfetcher.dto.response.MetaInfoResponseDto;
import com.stockfetcher.dto.response.WatchlistResponseDto;
import com.stockfetcher.exception.WatchlistAlreadyExistsException;
import com.stockfetcher.exception.WatchlistLimitExceededException;
import com.stockfetcher.model.MetaInfo;
import com.stockfetcher.model.Watchlist;
import com.stockfetcher.model.WatchlistMetaInfo;
import com.stockfetcher.repository.MetaInfoRepository;
import com.stockfetcher.repository.WatchlistRepository;
import com.stockfetcher.utils.GenericMapperUtil;

import jakarta.transaction.Transactional;

@Service
public class WatchlistService {

	private final WatchlistRepository watchlistRepository;
	private final MetaInfoRepository metaInfoRepository;

	private static final int WATCHLIST_LIMIT = 5;
	private static final int METAINFO_LIMIT = 25; // Limit of MetaInfo in a watchlist

	public WatchlistService(WatchlistRepository watchlistRepository, MetaInfoRepository metaInfoRepository) {
		this.watchlistRepository = watchlistRepository;
		this.metaInfoRepository = metaInfoRepository;
	}

	// Create Watchlist
	public WatchlistResponseDto createWatchlist(WatchlistRequestDto watchlistRequestDto) {
		long watchlistCount = watchlistRepository.countByUserId(watchlistRequestDto.getUserId());
		if (watchlistCount >= WATCHLIST_LIMIT) {
			throw new WatchlistLimitExceededException(
					"You have reached the limit of " + WATCHLIST_LIMIT + " watchlists.");
		}

		// Check if a watchlist with the same name already exists
		if (watchlistRepository.existsByNameAndUserId(watchlistRequestDto.getName(), watchlistRequestDto.getUserId())) {
			throw new WatchlistAlreadyExistsException(
					"Watchlist already exists with the name: " + watchlistRequestDto.getName());
		}

		Watchlist watchlist = GenericMapperUtil.convertToEntity(watchlistRequestDto, Watchlist.class);
		watchlist.setCreatedAt(LocalDateTime.now());
		Watchlist savedWatchlist = watchlistRepository.save(watchlist);
		return GenericMapperUtil.convertToDto(savedWatchlist, WatchlistResponseDto.class);
	}

	// Get Watchlist by ID
	public Watchlist getWatchlistById(Long id) {
		return watchlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Watchlist not found"));
	}

	// Get All Watchlists for a User
	public List<Watchlist> getAllWatchlistsForUser(Long userId) {
		return watchlistRepository.findByUserId(userId);
	}

	// Update Watchlist
	public WatchlistResponseDto updateWatchlist(Long id, WatchlistRequestDto watchlistRequestDto) {
		Watchlist existingWatchlist = watchlistRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));

		if (!existingWatchlist.getName().equalsIgnoreCase(watchlistRequestDto.getName()) && watchlistRepository
				.existsByNameAndUserId(watchlistRequestDto.getName(), watchlistRequestDto.getUserId())) {
			throw new WatchlistAlreadyExistsException(
					"Watchlist already exists with the name: " + watchlistRequestDto.getName());
		}

		existingWatchlist.setName(watchlistRequestDto.getName());
		existingWatchlist.setUpdatedAt(LocalDateTime.now());
		Watchlist updatedWatchlist = watchlistRepository.save(existingWatchlist);
		return GenericMapperUtil.convertToDto(updatedWatchlist, WatchlistResponseDto.class);
	}

	// Delete Watchlist
	public void deleteWatchlist(Long id) {
		if (!watchlistRepository.existsById(id)) {
			throw new RuntimeException("Watchlist not found");
		}
		watchlistRepository.deleteById(id);
	}

	@Transactional
	public void addMetaInfoToWatchlist(Long watchlistId, String symbol) {
		Watchlist watchlist = watchlistRepository.findById(watchlistId)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));

		MetaInfo metaInfo = metaInfoRepository.findBySymbol(symbol)
				.orElseThrow(() -> new RuntimeException("MetaInfo not found"));

		// Check if the relationship already exists
		boolean alreadyExists = watchlist.getWatchlistMetaInfos().stream()
				.anyMatch(watchlistMetaInfo -> watchlistMetaInfo.getMetaInfo().equals(metaInfo));

		if (alreadyExists) {
			throw new RuntimeException("This MetaInfo is already associated with the Watchlist.");
		}

		// Add MetaInfo to Watchlist
		watchlist.addMetaInfo(metaInfo);
		
		 System.out.println(" watchlist: " + watchlist);

		// Save owning side
		watchlistRepository.save(watchlist);
	}

	@Transactional
	public void removeMetaInfoFromWatchlist(Long watchlistId, String symbol) {
		Watchlist watchlist = watchlistRepository.findById(watchlistId)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));

		MetaInfo metaInfo = metaInfoRepository.findBySymbol(symbol)
				.orElseThrow(() -> new RuntimeException("MetaInfo not found"));

		// Remove MetaInfo from Watchlist
		watchlist.removeMetaInfo(metaInfo);

		// Save owning side
		watchlistRepository.save(watchlist);
	}

	// Get MetaInfos in a Watchlist
	public List<MetaInfoResponseDto> getMetaInfosInWatchlist(Long watchlistId, String sortField, String sortOrder,
			String searchQuery) {
		// Fetch the Watchlist entity by ID
		Watchlist watchlist = watchlistRepository.findById(watchlistId)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));

		// Extract MetaInfos from the join entity (WatchlistMetaInfo)
		List<MetaInfo> metaInfos = watchlist.getWatchlistMetaInfos().stream().map(WatchlistMetaInfo::getMetaInfo)
				.collect(Collectors.toList());

		// Convert the list of MetaInfo entities to DTOs
		List<MetaInfoResponseDto> metaInfoResponseDtoList = GenericMapperUtil.convertToDtoList(metaInfos,
				MetaInfoResponseDto.class);

		// Filter by search query
		if (searchQuery != null && !searchQuery.isEmpty()) {
			metaInfoResponseDtoList = metaInfoResponseDtoList.stream()
					.filter(metaInfo -> metaInfo.getSymbol().toLowerCase().contains(searchQuery.toLowerCase()))
					.collect(Collectors.toList());
		}

		// Sort the MetaInfos based on the sortField and sortOrder
		if ("symbol".equalsIgnoreCase(sortField)) {
			Comparator<MetaInfoResponseDto> comparator = Comparator.comparing(MetaInfoResponseDto::getSymbol);
			metaInfoResponseDtoList = sortOrder.equalsIgnoreCase("desc")
					? metaInfoResponseDtoList.stream().sorted(comparator.reversed()).collect(Collectors.toList())
					: metaInfoResponseDtoList.stream().sorted(comparator).collect(Collectors.toList());
		}

		return metaInfoResponseDtoList;
	}
}
