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
import java.net.URI;
import java.util.logging.Logger;


public class ImageClass implements Images {

    private static Logger Log = Logger.getLogger(ImageClass.class.getName());
    private final Hibernate hibernate = Hibernate.getInstance();

    Discovery discovery;
    Users usersServer;

    public ImageClass() throws IOException {
        discovery = new Discovery(Discovery.DISCOVERY_ADDR);
        discovery.start();
        findServer();

    }

    //TODO: Implementação podre, rever
    private void findServer(){
        URI userServiceURI = discovery.knownUrisOf("users");
        while (userServiceURI == null) {
            userServiceURI = discovery.knownUrisOf("users");
        }
       usersServer = new RestUsersClient(userServiceURI);
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
            Image img = new Image(userId, imageContents);
            hibernate.persist(img);
            return Result.ok(img.getImageId());

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

        return Result.ok(image.getContents());
    }

    @Override
    public Result<Void> deleteImage(String userId, String imageId, String password) {
        Log.info("deleteImage : image = " + imageId);

        Result<User> userResult = usersServer.getUser(userId, password);
        if (!userResult.isOK()) {
            return Result.error(userResult.error());
        }

        Result<byte[]> imageResult = getImage(userId, imageId);
        if (!imageResult.isOK()) {
            return Result.error(imageResult.error());
        }
        try {
            hibernate.delete(Image.class, imageId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Result.ok();
    }
}
