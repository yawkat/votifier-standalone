/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.votifier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * KeyPair wrapper with votifier serialization and generation methods.
 *
 * @author yawkat
 */
public class VotifierKeyPair {
    private static final int DEFAULT_KEY_SIZE = 2048;
    private static final boolean PRETTY = true;

    private final KeyPair pair;

    public VotifierKeyPair(KeyPair pair) {
        Objects.requireNonNull(pair);
        this.pair = pair;
    }

    KeyPair getPair() {
        return pair;
    }

    private void write(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("public_key");
        writer.value(getPubEncoded());
        writer.name("private_key");
        writer.value(getPrivEncoded());
        writer.endObject();
    }

    public void write(Writer writer, boolean pretty) throws IOException {
        JsonWriter w = new JsonWriter(writer);
        w.setIndent(pretty ? "    " : "");
        write(w);
    }

    public void write(Writer writer) throws IOException {
        write(writer, PRETTY);
    }

    public void write(Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            write(writer);
        }
    }

    public void write(File file) throws IOException {
        write(file.toPath());
    }

    public JsonObject toJson() {
        return toJson(new JsonObject());
    }

    public JsonObject toJson(JsonObject target) {
        target.addProperty("public_key", getPubEncoded());
        target.addProperty("private_key", getPrivEncoded());
        return target;
    }

    private String getPubEncoded() {
        return Base64.getEncoder().encodeToString(new X509EncodedKeySpec(pair.getPublic().getEncoded()).getEncoded());
    }

    private String getPrivEncoded() {
        return Base64.getEncoder().encodeToString(new PKCS8EncodedKeySpec(pair.getPrivate().getEncoded()).getEncoded());
    }

    public static VotifierKeyPair read(JsonObject source) throws Exception {
        byte[] publicKeyEnc = Base64.getDecoder().decode(source.get("public_key").getAsString());
        byte[] privateKeyEnc = Base64.getDecoder().decode(source.get("private_key").getAsString());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyEnc));
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyEnc));

        return new VotifierKeyPair(new KeyPair(publicKey, privateKey));
    }

    public static VotifierKeyPair read(Reader reader) throws Exception {
        return read(new JsonParser().parse(reader).getAsJsonObject());
    }

    public static VotifierKeyPair read(Path path) throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return read(reader);
        }
    }

    public static VotifierKeyPair read(File file) throws Exception {
        return read(file.toPath());
    }

    public static VotifierKeyPair generate() {
        return generate(DEFAULT_KEY_SIZE);
    }

    public static VotifierKeyPair generate(int keySize) {
        return generate(keySize, null);
    }

    public static VotifierKeyPair generate(int keySize, SecureRandom random) {
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (random == null) {
            generator.initialize(keySize);
        } else {
            generator.initialize(keySize, random);
        }
        return new VotifierKeyPair(generator.generateKeyPair());
    }
}
