package com.rahul.auth_service.utils;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.rahul.auth_service.dto.ApiException;
import com.rahul.auth_service.dto.ApiResponseCodes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwe;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.symmetric.AES.AESGMAC;
import org.springframework.beans.factory.annotation.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenUtils {

//    @Value("${jwt.token}")
//    private static String jwtSecretKey;
//
//    @Value("${jwt.token.expiry}")
//    private static String jwtTokenExpiry;

    // create separate encryption and sign key

    // Todo : Keep this in properties file with encryption
    private static final String jwtSecretKey = "2b7e151628aed2a6abf7158809cf4f3c";
    private static final Long jwtTokenExpiry= 86400000L;


    public static String generateToken(String data) throws Exception {

        String encryptedToken = getEncryptedToken(data);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());

        return Jwts.builder()
            .subject(encryptedToken)
            .expiration(new Date(System.currentTimeMillis() + jwtTokenExpiry))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private static String getDecryptedToken(String encryptedToken) throws Exception{
         SecretKey key = new SecretKeySpec(jwtSecretKey.getBytes(), 0, jwtSecretKey.length(), "AES");
        JWEObject jweObject = JWEObject.parse(encryptedToken);
        jweObject.decrypt(new DirectDecrypter(key));

        // Return the decrypted payload
        return jweObject.getPayload().toString();
    }

    private static String getEncryptedToken(String data) throws Exception {

        JWEObject jweObject = new JWEObject(
            new com.nimbusds.jose.JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM),
            new Payload(data)
        );
        jweObject.encrypt(new DirectEncrypter(new SecretKeySpec(jwtSecretKey.getBytes(), 0, jwtSecretKey.length(), "AES")));
        return jweObject.serialize();
    }

    private static void verifyJwtToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
        JwtParser jwtParser = Jwts.parser()
            .verifyWith(key)
            .build();
        try {
            jwtParser.parse(token);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCodes.SERVER_ERROR.getTitle(),
                ApiResponseCodes.SERVER_ERROR.getMessage(),
                ApiResponseCodes.SERVER_ERROR,
                e);
        }
    }

    public static String getDataFromToken(String token){

        String decryptedToken = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());

            JwtParser parser = Jwts.parser()
                .verifyWith(key)
                .build();

            Jws<Claims> claimsJws = parser.parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();

            if (claims == null) {
                return null;
            }
            decryptedToken = getDecryptedToken(claims.getSubject());
            if (StringUtils.isBlank(decryptedToken)) {
                return null;
            }
        }catch (Exception e) {
            return null;
        }

        return decryptedToken;

    }
}
