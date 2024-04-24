package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {
    private final Queue queue;
    private final RabbitTemplate template;
    private final SpotifyApi spotifyApi;
    private final AlbumRepository albumRepository;
    private final UsersService usersService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Album buyAlbumByUser(Album album) {

        album.setUsers(getUser());

        try {
            albumRepository.findByIdSpotifyAndUsers(album.getIdSpotify(), getUser());
        }catch(DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("User already owns this album");
        }

        Album albumSaved = albumRepository.save(album);

        // Generate Rabbit Queue to Debit the sale in User's Wallet
        WalletDto walletDto = new WalletDto(albumSaved.getUsers().getEmail(), albumSaved.getValue());
        this.template.convertAndSend(queue.getName(), walletDto);

        log.info("Buy album by user {}", albumSaved.getUsers().getEmail());
        return albumSaved;
    }

    @Transactional(readOnly = true)
    public List<Album> getAlbumByUser(){
        log.info("Get album by User");
        return albumRepository.findAllByUsers(getUser());
    }

    @Transactional(readOnly = true)
    public List<AlbumModel> getAllAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        log.info("Searching for {}", search);
        return this.spotifyApi.getAlbums(search);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAlbumByID(Long id){
        Album albumToRemove = albumRepository.findByIdAndUsers(id, getUser());

        log.info("Deleting album by ID: {}", id);
        albumRepository.delete(albumToRemove);
    }

    // GET AUTHENTICATED USER
    private Users getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return usersService.findByEmail(username);
    }
}
