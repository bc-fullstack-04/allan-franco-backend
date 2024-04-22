package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;

import br.com.sysmap.bootcamp.domain.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@Tag(name="Albums", description="Albums API")
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "Buy an album")
    @PostMapping("/sale")
    public ResponseEntity<Album> buyAlbumByUser(@RequestBody Album album) {
        return ResponseEntity.ok(this.albumService.buyAlbumByUser(album));
    }

    @Operation(summary = "Get all albums from my collection")
    @GetMapping("/my-collection")
    public ResponseEntity<List<Album>> getAlbumByUser(){
        return ResponseEntity.ok(this.albumService.getAlbumByUser());
    }

    @Operation(summary = "Get all albums from Spotify service by Text parameter")
    @GetMapping("/all")
    public ResponseEntity<List<AlbumModel>> getAllAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException{
        return ResponseEntity.ok(this.albumService.getAllAlbums(search));
    }

    @Operation(summary = "Remove an album by ID")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeAlbumByID(@PathVariable Long id){
        this.albumService.removeAlbumByID(id);
        return ResponseEntity.noContent().build();
    }
}
