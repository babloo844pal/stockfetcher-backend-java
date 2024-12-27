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
import com.stockfetcher.repository.MetaInfoRepository;
import com.stockfetcher.repository.WatchlistRepository;
import com.stockfetcher.utils.GenericMapperUtil;

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

	// Add MetaInfo to Watchlist
	public void addMetaInfoToWatchlist(Long watchlistId, Long metaInfoId) {
		Watchlist watchlist = watchlistRepository.findById(watchlistId)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));

		MetaInfo metaInfo = metaInfoRepository.findById(metaInfoId)
				.orElseThrow(() -> new RuntimeException("MetaInfo not found"));

		// Check for MetaInfo limit in the watchlist
		if (watchlist.getMetaInfos().size() >= METAINFO_LIMIT) {
			throw new WatchlistLimitExceededException(
					"Cannot add more than " + METAINFO_LIMIT + " MetaInfo entries to this watchlist.");
		}

		watchlist.getMetaInfos().add(metaInfo);
		watchlistRepository.save(watchlist);
	}

	// Remove MetaInfo from Watchlist
	public void removeMetaInfoFromWatchlist(Long watchlistId, Long metaInfoId) {
		Watchlist watchlist = watchlistRepository.findById(watchlistId)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));

		MetaInfo metaInfo = metaInfoRepository.findById(metaInfoId)
				.orElseThrow(() -> new RuntimeException("MetaInfo not found"));

		watchlist.getMetaInfos().remove(metaInfo);
		watchlistRepository.save(watchlist);
	}

	// Get MetaInfos in a Watchlist
	public List<MetaInfoResponseDto> getMetaInfosInWatchlist(Long watchlistId, String sortField, String sortOrder,
			String searchQuery) {
		Watchlist watchlist = watchlistRepository.findById(watchlistId)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));

		List<MetaInfo> metaInfos = watchlist.getMetaInfos().stream().collect(Collectors.toList());
		List<MetaInfoResponseDto> metaInfoResponseDtoList = GenericMapperUtil.convertToDtoList(metaInfos,
				MetaInfoResponseDto.class);

		// Filter by search query
		if (searchQuery != null && !searchQuery.isEmpty()) {
			metaInfoResponseDtoList = metaInfoResponseDtoList.stream()
					.filter(metaInfo -> metaInfo.getSymbol().toLowerCase().contains(searchQuery.toLowerCase()))
					.collect(Collectors.toList());
		}

		// Sort the MetaInfos
		if ("symbol".equalsIgnoreCase(sortField)) {
			Comparator<MetaInfoResponseDto> comparator = Comparator.comparing(MetaInfoResponseDto::getSymbol);
			metaInfoResponseDtoList = sortOrder.equalsIgnoreCase("desc")
					? metaInfoResponseDtoList.stream().sorted(comparator.reversed()).collect(Collectors.toList())
					: metaInfoResponseDtoList.stream().sorted(comparator).collect(Collectors.toList());
		}

		return metaInfoResponseDtoList;
	}
}
