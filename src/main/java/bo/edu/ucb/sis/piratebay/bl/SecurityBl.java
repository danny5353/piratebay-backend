package bo.edu.ucb.sis.piratebay.bl;

import bo.edu.ucb.sis.piratebay.dao.UserDao;
import bo.edu.ucb.sis.piratebay.model.TokenRefreshModel;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecurityBl {

    private UserDao userDao;

    @Value("${piratebay.security.salt}")
    private String salt;

    @Value("${piratebay.security.secretJwt}")
    private String secretJwt;

    @Autowired
    public SecurityBl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Este metodo recibe el usuario y password en plano, consulta a la base de datos, enviando el password con
     * SHA256 + SALT, y si esta bien genera el token JWT
     * @param username Nombre de usuario en plano
     * @param password Contraseña en plano
     * @return Token JWT.
     */
    public Map<String, String> authenticate(String username, String password) {
        Map<String, String> result = new HashMap<>();
        String sha256hex = Hashing.sha256()
                .hashString(password+salt, StandardCharsets.UTF_8)
                .toString(); // TODO Repetir el algoritmo de hash N veces
        Integer userId = userDao.findUserIdByUsernameAndPassword(username, sha256hex);
        if (userId != null ) {
            result.put("authentication", generateJWT(userId, 2, "AUTHN", userDao.findAllFeatureCodeByUserId(userId)));
            result.put("refresh", generateJWT(userId, 6, "REFRESH", null));
            return result;
        } else {
            return null;
        }
    }

    public Map<String, String> refresh(TokenRefreshModel tokenRefreshModel) {
        // Lo unico que estamos haciendo es decodificar el token.
        String tokenJwT = tokenRefreshModel.getRefreshToken();
        DecodedJWT decodedJWT = JWT.decode(tokenJwT);
        String idUsuario = decodedJWT.getSubject();

        if(!"REFRESH".equals(decodedJWT.getClaim("type").asString()) ) {
            throw new RuntimeException("El token proporcionado no es un token valido para actualizar");
        }

        // El siguiente código valida si el token es bueno y ademas es un token de authentication
        Algorithm algorithm = Algorithm.HMAC256(secretJwt);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("PirateBay")
                .build();
        verifier.verify(tokenJwT);


        Map<String, String> result = new HashMap<>();
        Integer userIdAsInt = Integer.parseInt(idUsuario);
        result.put("authentication", generateJWT(userIdAsInt, 6, "AUTHN", userDao.findAllFeatureCodeByUserId(userIdAsInt)));
        result.put("refresh", generateJWT(Integer.parseInt(idUsuario), 3, "REFRESH", null));
        return result;

    }

    private String generateJWT(Integer userId, int minutes, String type, List<String> features) {
        LocalDateTime expiresAt = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(minutes);
        String token = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretJwt);
            if (features != null) {
                token = JWT.create()
                        .withIssuer("PirateBay")
                        .withClaim("type", type)
                        .withArrayClaim("features", features.toArray(new String[0]))
                        .withSubject(userId.toString())
                        .withExpiresAt(Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant()))
                        .sign(algorithm);
            } else {
                token = JWT.create()
                        .withIssuer("PirateBay")
                        .withClaim("type", type)
                        .withSubject(userId.toString())
                        .withExpiresAt(Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant()))
                        .sign(algorithm);
            }

        } catch (JWTCreationException ex){
            throw new RuntimeException(ex);
        }
        return token;
    }
}
