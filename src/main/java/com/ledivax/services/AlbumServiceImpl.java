package com.ledivax.services;

import com.ledivax.annotations.Loggable;
import com.ledivax.dao.AccountDao;
import com.ledivax.dao.AlbumDao;
import com.ledivax.dao.SongDao;
import com.ledivax.dto.album.AlbumCreateDto;
import com.ledivax.dto.album.AlbumInfoDto;
import com.ledivax.dto.album.AlbumUpdateDto;
import com.ledivax.exceptions.DataChangesException;
import com.ledivax.models.AEntity;
import com.ledivax.models.Account;
import com.ledivax.models.AccountDetails;
import com.ledivax.models.Album;
import com.ledivax.models.Song;
import com.ledivax.services.api.AlbumService;
import com.ledivax.util.Convertor;
import com.ledivax.util.Paginator;
import com.ledivax.util.Unpacker;
import com.ledivax.util.mappers.AlbumMapper;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlbumServiceImpl implements AlbumService {

    private final Long numberOfUsersAtTime;
    private final Double percentageOfSimilarity;
    private final AlbumDao albumDao;
    private final AccountDao accountDao;
    private final SongDao songDao;
    private final AlbumMapper albumMapper;
    private final MannWhitneyUTest mannWhitneyUTest;


    public AlbumServiceImpl(AlbumDao albumDao, AccountDao accountDao, SongDao songDao, AlbumMapper albumMapper,
                            MannWhitneyUTest mannWhitneyUTest,
                            @Value("${recommendation.numberOfUsersAtTime}") Long numberOfUsersAtTime,
                            @Value("${recommendation.percentageOfSimilarity}") Double percentageOfSimilarity) {
        this.albumDao = albumDao;
        this.accountDao = accountDao;
        this.songDao = songDao;
        this.albumMapper = albumMapper;
        this.mannWhitneyUTest = mannWhitneyUTest;
        this.numberOfUsersAtTime = numberOfUsersAtTime;
        this.percentageOfSimilarity = percentageOfSimilarity;
    }

    @Override
    @Loggable
    public Long save(AlbumCreateDto albumDto) {
        Account account = accountDao.findById(albumDto.getCreatorId());

        Album album = albumMapper.toEntity(albumDto, account);

        return albumDao.save(album);
    }


    @Override
    @Loggable
    public void updateData(Long id, AlbumUpdateDto albumDto) {
        Album album = albumDao.findByIdWithCreator(id);

        album.setTitle(albumDto.getTitle());
        albumDao.update(album);
    }

    @Override
    @Loggable
    public AlbumInfoDto findById(Long id) {
        Album album = albumDao.findById(id);
        return albumMapper.toAlbumInfoDto(album);
    }

    @Override
    @Loggable
    public void deleteById(Long id) {
        albumDao.deleteById(id);
    }

    @Override
    @Loggable
    public void addSongIn(Long albumId, Long songId) {
        Album album = albumDao.findByIdWithCreator(albumId);
        Song song = songDao.findById(songId);
        if (!album.getSongsIn().contains(song)) {
            album.getSongsIn().add(song);
        } else {
            throw new DataChangesException("Album already contains such a song");
        }
    }

    @Override
    @Loggable
    public void removeSongIn(Long albumId, Long songId) {
        Album album = albumDao.findByIdWithCreator(albumId);
        Song song = songDao.findById(songId);

        if (album.getSongsIn().contains(song)) {
            album.getSongsIn().remove(song);
        } else {
            throw new DataChangesException("Album does not contain such a song");
        }
    }

    @Override
    @Loggable
    public List<AlbumInfoDto> findSavedByAccountId(Long accountId) {
        return albumMapper.toAlbumInfoDtoList(
                albumDao.findSavedFromByAccountId(accountId)
        );
    }

    @Override
    @Loggable
    public List<AlbumInfoDto> findByCreatorId(Long accountId) {
        return albumMapper.toAlbumInfoDtoList(
                albumDao.findCreatedFromAccountId(accountId)
        );
    }

    @Override
    @Loggable
    public List<AlbumInfoDto> findAll(String pageNumber, String limit) {
        Integer pageNumberInteger = Convertor.stringToInteger(pageNumber);
        Integer limitInteger = Convertor.stringToInteger(limit);

        limitInteger = Paginator.limitingMinimumValueToOne(limitInteger);

        Long totalCount = albumDao.getTotalCount();
        Integer firstResult = Paginator.getFirstElement(pageNumberInteger, totalCount, limitInteger);


        return albumMapper.toAlbumInfoDtoList(
                albumDao.findAll(Math.toIntExact(firstResult), limitInteger)
        );
    }

    @Override
    @Loggable
    public List<AlbumInfoDto> findByTitle(String title, String pageNumber, String limit) {
        Integer pageNumberInteger = Convertor.stringToInteger(pageNumber);
        Integer limitInteger = Convertor.stringToInteger(limit);

        limitInteger = Paginator.limitingMinimumValueToOne(limitInteger);

        Long totalCount = albumDao.getTotalCount();
        Integer firstResult = Paginator.getFirstElement(pageNumberInteger, totalCount, limitInteger);


        return albumMapper.toAlbumInfoDtoList(
                albumDao.findByTitle(title, Math.toIntExact(firstResult), limitInteger)
        );
    }

    @Override
    @Loggable
    public List<AlbumInfoDto> findRecommendedFor(AccountDetails accountDetails, String limit) {
        Integer limitInteger = Convertor.stringToInteger(limit);

        limitInteger = Paginator.limitingMinimumValueToOne(limitInteger);

        Account account = accountDao.findWithSavedAlbumsById(accountDetails.getId());

        Double[] savedAlbums = getAccountSavedAlbums(account);

        Set<Long> recommendation;
        if (savedAlbums.length < 3) {
            recommendation = getRandomRecommendation(savedAlbums, limitInteger);
        } else {
            recommendation = getRecommendation(savedAlbums, limitInteger);
        }

        return albumMapper.toAlbumInfoDtoList(albumDao.findByIds(recommendation));
    }

    private Set<Long> getRandomRecommendation(Double[] savedAlbums, Integer limit) {
        Set<Long> savedAlbumsLong = Arrays.stream(savedAlbums)
                .map(Double::longValue)
                .collect(Collectors.toSet());

        return albumDao.findRandomExcept(limit, savedAlbumsLong)
                .stream()
                .map(AEntity::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> getRecommendation(Double[] savedAlbums, Integer limit) {
        Set<Long> recommendation = new HashSet<>();

        Long id = 1L;

        double[] savedAlbumsArr = Unpacker.convertToPrimitiveDoubleArray(savedAlbums);

        while (recommendation.size() < limit) {
            List<Account> accounts = accountDao.findWithSavedAlbumsByIdInBetween(id, id + numberOfUsersAtTime);
            id += numberOfUsersAtTime;

            if (accounts.size() == 0) {
                break;
            }

            for (Account account : accounts) {
                double[] accountSavedAlbumsArr = getAccountPrimitiveSavedAlbums(account);

                if (accountSavedAlbumsArr.length > 0 && isSimilar(savedAlbumsArr, accountSavedAlbumsArr)) {
                    addRecommendedAlbums(savedAlbums, recommendation, accountSavedAlbumsArr, limit);
                }
            }
        }

        return recommendation;
    }

    private double[] getAccountPrimitiveSavedAlbums(Account account) {
        return account.getSavedAlbums()
                .stream()
                .map(AEntity::getId)
                .mapToDouble(Long::doubleValue)
                .toArray();
    }

    private Double[] getAccountSavedAlbums(Account account) {
        return account.getSavedAlbums()
                .stream()
                .map(album -> album.getId().doubleValue())
                .toArray(Double[]::new);
    }

    private boolean isSimilar(double[] savedAlbumsArr, double[] accountSavedAlbumsArr) {
        return mannWhitneyUTest.mannWhitneyUTest(savedAlbumsArr, accountSavedAlbumsArr) >= percentageOfSimilarity;
    }

    private void addRecommendedAlbums(Double[] savedAlbums, Set<Long> recommendation, double[] accountSavedAlbumsArr,
                                      Integer limit) {
        Arrays.stream(accountSavedAlbumsArr)
                .filter(albumId -> !Arrays.asList(savedAlbums).contains(albumId))
                .limit(limit - recommendation.size())
                .forEach(albumId -> recommendation.add((long) albumId));
    }
}
