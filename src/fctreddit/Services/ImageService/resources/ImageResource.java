package fctreddit.Services.ImageService.resources;

import fctreddit.Services.ImageService.java.ImageClass;
import fctreddit.api.Interfaces.Images;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Rest.RestImage;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;
import java.util.logging.Logger;

public class ImageResource implements RestImage {

    private static Logger Log = Logger.getLogger(ImageResource.class.getName());
    private final Images impl = new ImageClass();

    public ImageResource() throws IOException {
    }

    @Override
    public String createImage(String userId, byte[] imageContents, String password) {
        Result<String> res = impl.createImage(userId, imageContents, password);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public byte[] getImage(String userId, String imageId) {
        Result<byte[]> res = impl.getImage(userId, imageId);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
        return res.value();
    }

    @Override
    public void deleteImage(String userId, String imageId, String password) {
        Result<Void> res = impl.deleteImage(userId, imageId, password);
        if (!res.isOK())
            throw new WebApplicationException(errorCodeToStatus(res.error()));
    }

    private static Status errorCodeToStatus(Result.ErrorCode error ) {
        return switch (error) {
            case NOT_FOUND -> Response.Status.NOT_FOUND;         // 404
            case CONFLICT -> Response.Status.CONFLICT;           // 409
            case FORBIDDEN -> Response.Status.FORBIDDEN;         // 403
            case BAD_REQUEST -> Response.Status.BAD_REQUEST;     // 400
            case NOT_IMPLEMENTED -> Response.Status.NOT_IMPLEMENTED; // 501
            default -> Response.Status.INTERNAL_SERVER_ERROR;    // 500
        };
    }
}
