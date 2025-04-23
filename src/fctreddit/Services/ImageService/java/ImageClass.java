package fctreddit.Services.ImageService.java;

import fctreddit.Clients.rest.RestUsersClient;
import fctreddit.Hibernate;
import fctreddit.Discovery.Discovery    ;
import fctreddit.api.Image;
import fctreddit.api.Interfaces.Images;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Interfaces.Users;
import fctreddit.api.User;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Logger;


public class ImageClass implements Images {

    public static final int PORT = 8081;
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    private static Logger Log = Logger.getLogger(ImageClass.class.getName());
    private final Hibernate hibernate = Hibernate.getInstance();

    Discovery discovery;
    Users usersServer;
    String ip = InetAddress.getLocalHost().getHostAddress();
    String serverURI = String.format(SERVER_URI_FMT, ip, PORT);

    public ImageClass() throws IOException {
        discovery = new Discovery(Discovery.DISCOVERY_ADDR); // Sem anunciar
        discovery.start();
        usersServer = discovery.findServer("Users");

    }


    @Override
    public Result<String> createImage(String userId, byte[] imageContents, String password) {
        Log.info("createImage");

        if (imageContents == null || imageContents.length == 0) {
            Log.info("Image contents are empty.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        Result<User> userResult = usersServer.getUser(userId, password);
        if (!userResult.isOK()) {
            return Result.error(userResult.error());
        }

        try {

            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = UUID.randomUUID().toString() + ".jpg";
            Path filePath = Paths.get(uploadDir, filename);

            Files.write(filePath, imageContents);
            Log.info("Imagem escrita no disco: " + filePath.toString());

            Image img = new Image(userId, filePath.toString());
            hibernate.persist(img);
            Log.info("Imagem persistida");

            String imageUri = String.format("%s/image/%s/%s",serverURI, userId, img.getImageId().toString());
            return Result.ok(imageUri);

        } catch (Exception e) {
            Log.info("Failed to save image: " + e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<byte[]> getImage(String userId, String imageId) {
        Log.info("getImage : " + imageId);

        if(imageId == null) {
            Log.info("ImageId null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        Image image = null;
        try {
            image = hibernate.get(Image.class, imageId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        if (image == null) {
            Log.info("Image does not exist.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        try {
            Path path = Paths.get(image.getFilePath());
            byte[] imageData = Files.readAllBytes(path);
            return Result.ok(imageData);
        } catch (IOException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Result<Void> deleteImage(String userId, String imageId, String password) {
        Log.info("deleteImage : image = " + imageId);

        Result<User> userResult = usersServer.getUser(userId, password);
        if (!userResult.isOK()) {
            return Result.error(userResult.error());
        }

        Image img = hibernate.get(Image.class, imageId);
        if (img == null || !img.getUserId().equals(userId)) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        try {
            Files.deleteIfExists(Paths.get(img.getFilePath()));
            hibernate.delete(img);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Result.ok();
    }
}
