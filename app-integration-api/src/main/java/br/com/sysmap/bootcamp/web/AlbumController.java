package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;

import br.com.sysmap.bootcamp.domain.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@Tag(name="Albums", description="Albums API")
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "Buy an album")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Album purchased successfully"),
            @ApiResponse(responseCode="400", description="Invalid input data"),
            @ApiResponse(responseCode="401", description="Unauthorized"),
            @ApiResponse(responseCode="500", description="Internal server error")
    })
    @PostMapping("/sale")
    public ResponseEntity<String> buyAlbumByUser(@RequestBody Album album) {
        try {
            this.albumService.buyAlbumByUser(album);
            return ResponseEntity.ok("Album purchased successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ooh something went wrong :(");
        }
    }

    @Operation(summary = "Get all albums from my collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Album purchased successfully"),
            @ApiResponse(responseCode="400", description="Invalid input data"),
            @ApiResponse(responseCode="401", description="Unauthorized"),
            @ApiResponse(responseCode="500", description="Internal server error")
    })
    @GetMapping("/my-collection")
    public ResponseEntity<?> getAlbumByUser(){
        try {
            return ResponseEntity.ok(this.albumService.getAlbumByUser());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User's album not found");
        }
    }

    @Operation(summary = "Get all albums from Spotify service by Text parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Album purchased successfully"),
            @ApiResponse(responseCode="400", description="Invalid input data"),
            @ApiResponse(responseCode="500", description="Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException{
        try {
            return ResponseEntity.ok(this.albumService.getAllAlbums(search));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Any album found");
        }
    }

    @Operation(summary = "Remove an album by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Album removed successfully"),
            @ApiResponse(responseCode="400", description="Invalid input data"),
            @ApiResponse(responseCode="401", description="Unauthorized"),
            @ApiResponse(responseCode="500", description="Internal server error")
    })
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeAlbumByID(@PathVariable Long id){
        try {
            this.albumService.removeAlbumByID(id);
            return ResponseEntity.ok("Album removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ooh something went wrong :(");
        }
    }
}
