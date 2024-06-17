package io.playlistify.utils;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtils {

  private final byte[] secretKeyBytes;
  private final byte[] secretSaltBytes;

  public EncryptionUtils(
      @Value("${app.secret-key}") final byte[] secretKeyBytes,
      @Value("${app.secret-salt}") final byte[] secretSaltBytes) {
    this.secretKeyBytes = secretKeyBytes;
    this.secretSaltBytes = secretSaltBytes;
  }

  public String encrypt(final String data) {
    try {
      final var cipherPair = getCipherPair();
      return Encryptors.text(cipherPair.secret, cipherPair.salt).encrypt(data);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String decrypt(final String encryptedData) {
    try {
      final var cipherPair = getCipherPair();
      return Encryptors.text(cipherPair.secret, cipherPair.salt)
          .decrypt(new String(Base64.getDecoder().decode(encryptedData)));
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private CipherPair getCipherPair() {
    final byte[] decodedKey = Base64.getDecoder().decode(this.secretKeyBytes);
    final byte[] salt = Base64.getDecoder().decode(this.secretSaltBytes);
    return new CipherPair(new String(decodedKey), new String(salt));
  }

  private CipherPair generateCipherPair() {
    try {
      final String secret =
          Base64.getEncoder().encodeToString(KeyGenerators.string().generateKey().getBytes());
      final String salt =
          Base64.getEncoder().encodeToString(KeyGenerators.string().generateKey().getBytes());
      return new CipherPair(secret, salt);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  record CipherPair(String secret, String salt) {}
}
