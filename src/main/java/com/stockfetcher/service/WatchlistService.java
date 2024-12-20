package com.stockfetcher.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stockfetcher.dto.request.WatchlistRequestDto;
import com.stockfetcher.dto.response.WatchlistResponseDto;
import com.stockfetcher.dto.response.WatchlistStockResponseDto;
import com.stockfetcher.exception.WatchlistAlreadyExistsException;
import com.stockfetcher.exception.WatchlistLimitExceededException;
import com.stockfetcher.model.WatchlistStock;
import com.stockfetcher.processor.Watchlist;
import com.stockfetcher.repository.WatchlistRepository;
import com.stockfetcher.repository.WatchlistStockRepository;
import com.stockfetcher.utils.GenericMapperUtil;

@Service
public class WatchlistService {

	private final WatchlistRepository watchlistRepository;
	private final WatchlistStockRepository watchlistStockRepository;

	private static final int WATCHLIST_LIMIT = 5;
	private static final int STOCK_LIMIT = 25; // Limit of stocks in a watchlist

	public WatchlistService(WatchlistRepository watchlistRepository,
			WatchlistStockRepository watchlistStockRepository) {
		this.watchlistRepository = watchlistRepository;
		this.watchlistStockRepository = watchlistStockRepository;
	}

	// Create Watchlist
	public WatchlistResponseDto createWatchlist(WatchlistRequestDto watchlistRequestDto) {
		long watchlistCount = watchlistRepository.countByUserId(watchlistRequestDto.getUserId());
		if (watchlistCount >= WATCHLIST_LIMIT) {
			throw new WatchlistLimitExceededException(
					"You have reached the limit of " + WATCHLIST_LIMIT + " watchlists.");
		}

		// Check if a watchlist with the same name already exists
		if (watchlistRepository.existsByName(watchlistRequestDto.getName())) {
			throw new WatchlistAlreadyExistsException(
					"Watchlist already exists with the name: " + watchlistRequestDto.getName());
		}

		Watchlist watchlist = GenericMapperUtil.convertToEntity(watchlistRequestDto, Watchlist.class);
		watchlist.setCreatedAt(LocalDateTime.now());
		Watchlist watchlistresponse = watchlistRepository.save(watchlist);
		WatchlistResponseDto watchlistResponseDto = GenericMapperUtil.convertToDto(watchlistresponse,
				WatchlistResponseDto.class);
		return watchlistResponseDto;
	}

	// Get Watchlist by ID
	public Watchlist getWatchlistById(Long id) {
		return watchlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Watchlist not found"));
	}

	// Get Watchlist by ID
	public List<WatchlistResponseDto> getAllWatchlist() {
		List<Watchlist> watchlistList = watchlistRepository.findAll();
		List<WatchlistResponseDto> watchlistResponseDtoList = GenericMapperUtil.convertToDtoList(watchlistList,
				WatchlistResponseDto.class);
		return watchlistResponseDtoList;
	}

	// Get All Watchlists for a User
	public List<Watchlist> getAllWatchlistsForUser(Long userId) {
		return watchlistRepository.findByUserId(userId);
	}

	// Update Watchlist
	public WatchlistResponseDto updateWatchlist(Long id, WatchlistRequestDto watchlistRequestDto) {
		if (watchlistRepository.existsByName(watchlistRequestDto.getName())) {
			throw new WatchlistAlreadyExistsException(
					"Watchlist already exists with the name: " + watchlistRequestDto.getName());
		}
		Optional<Watchlist> watchlistdb = watchlistRepository.findById(id);
		Watchlist watchlist = GenericMapperUtil.convertToEntity(watchlistRequestDto, Watchlist.class);
		watchlist.setName(watchlistRequestDto.getName());
		watchlist.setId(id);
		watchlist.setCreatedAt(watchlistdb.get().getCreatedAt());
		watchlist.setUpdatedAt(LocalDateTime.now());
		Watchlist watchlistresponse = watchlistRepository.save(watchlist);
		WatchlistResponseDto watchlistResponseDto = GenericMapperUtil.convertToDto(watchlistresponse,
				WatchlistResponseDto.class);
		return watchlistResponseDto;
	}

	// Delete Watchlist
	public void deleteWatchlist(Long id) {
		watchlistRepository.deleteById(id);
	}

	// Add stock to watchlist
	public WatchlistStock addStockToWatchlist(Long watchlistId, String stockSymbol) {
		long stockCount = watchlistStockRepository.countByWatchlistId(watchlistId);
		if (stockCount >= STOCK_LIMIT) {
			throw new WatchlistLimitExceededException(
					"Cannot add more than " + STOCK_LIMIT + " stocks to this watchlist.");
		}

		Watchlist watchlist = watchlistRepository.findById(watchlistId)
				.orElseThrow(() -> new RuntimeException("Watchlist not found"));
		WatchlistStock stock = new WatchlistStock();
		stock.setStockSymbol(stockSymbol);
		stock.setAddedAt(LocalDateTime.now());
		stock.setWatchlist(watchlist);

		return watchlistStockRepository.save(stock);
	}

	// Delete stock from watchlist
	public void deleteStockFromWatchlist(Long watchlistId,Long stockId) {
		watchlistStockRepository.deleteById(stockId);
	}

	// Get stocks in a watchlist
	public List<WatchlistStockResponseDto> getStocksInWatchlist(Long watchlistId, String sortField, String sortOrder,
			String searchQuery) {
		List<WatchlistStock> stocksList = watchlistStockRepository.findByWatchlistId(watchlistId);
		List<WatchlistStockResponseDto> watchlistStockResponseDtoList = GenericMapperUtil.convertToDtoList(stocksList,
				WatchlistStockResponseDto.class);

		// Filter by search query
		if (searchQuery != null && !searchQuery.isEmpty()) {
			watchlistStockResponseDtoList = watchlistStockResponseDtoList.stream()
					.filter(stock -> stock.getStockSymbol().toLowerCase().contains(searchQuery.toLowerCase()))
					.collect(Collectors.toList());
		}

		// Sort the stocks
		if ("stockSymbol".equalsIgnoreCase(sortField)) {
			Comparator<WatchlistStockResponseDto> comparator = Comparator
					.comparing(WatchlistStockResponseDto::getStockSymbol);
			watchlistStockResponseDtoList = sortOrder.equalsIgnoreCase("desc")
					? watchlistStockResponseDtoList.stream().sorted(comparator.reversed()).collect(Collectors.toList())
					: watchlistStockResponseDtoList.stream().sorted(comparator).collect(Collectors.toList());
		}

		return watchlistStockResponseDtoList;
	}

}
