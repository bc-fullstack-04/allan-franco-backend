package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("wallet")
@Tag(name="Wallet", description = "Wallet API")
public class WalletController {
    private final WalletService walletService;

    @Operation(summary = "Credit value in wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Value added successfully to wallet"),
            @ApiResponse(responseCode="400", description="Invalid input data"),
            @ApiResponse(responseCode="401", description="Unauthorized"),
            @ApiResponse(responseCode="500", description="Internal server error")
    })
    @PostMapping("/credit/{value}")
    public ResponseEntity<String> addCreditToWalletByUser(@PathVariable BigDecimal value) {
        try {
            this.walletService.addCreditToWalletByUser(value);
            return ResponseEntity.ok("Value added successfully to wallet");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ooh something went wrong :(");
        }
    }

    @Operation(summary = "My Wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Wallet showed successfully"),
            @ApiResponse(responseCode="400", description="Invalid input data"),
            @ApiResponse(responseCode="401", description="Unauthorized"),
            @ApiResponse(responseCode="500", description="Internal server error")
    })
    @GetMapping()
    public ResponseEntity<?> getWalletByUser(){
        try {
            return ResponseEntity.ok(this.walletService.getWalletByUser());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User's wallet not found");
        }
    }
}
