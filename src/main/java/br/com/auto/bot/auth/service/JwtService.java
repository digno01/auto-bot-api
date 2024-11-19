package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.model.permissoes.PerfilAcesso;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    protected String secretKey;

    @Value("${security.jwt.expiration-time}")
    protected long jwtExpiration;

    @Value("${security.jwt.refresh-expiration-time}")
    protected long refreshExpiration; // Novo campo para tempo de expiração do refresh token

    @Value("${security.jwt.recovery-password-expiration-time}")
    protected long timeExpirationRecoveryToken; // Novo campo para tempo de expiração do refresh token

    @Autowired
    private PerfilAcessoService perfilAcessoService;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateRecoverToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, timeExpirationRecoveryToken);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        User user = (User) userDetails;
        List<PerfilAcesso> listaPerfilAcessoPorSistema = perfilAcessoService.findAll();
        List<String> roles = new ArrayList<>();
        user.getAuthorities().forEach(e -> {
                    var pfacee = listaPerfilAcessoPorSistema.stream().filter(pf -> {
                        String stParaComparar = "ROLE_" + pf.getPerfil();
                        return stParaComparar.equals(e.getAuthority());
                    }).collect(Collectors.toList());
                    if (!pfacee.isEmpty()) {
                        roles.add(e.getAuthority());
                    }
                }
        );

        // Adiciona as roles ao extraClaims
        extraClaims.put("roles", roles);
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration); // Usando a nova expiração
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public long getRefreshExpirationTime() {
        return refreshExpiration; // Método para obter a expiração do refresh token
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        User user = (User) userDetails;
        extraClaims.put("id", user.getId());
        extraClaims.put("nome", user.getNome());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    protected Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    protected Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    protected Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
