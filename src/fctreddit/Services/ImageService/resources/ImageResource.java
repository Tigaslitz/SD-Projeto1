package fctreddit.Services.ImageService.resources;

import fctreddit.Services.ImageService.java.ImageClass;
import fctreddit.api.Interfaces.Images;
import fctreddit.api.Interfaces.Result;
import fctreddit.api.Rest.RestImage;
import jakarta.ws.rs.WebApplicationException;

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

    private static Throwable errorCodeToStatus( Result.ErrorCode error ) {
        var status =  switch( error) {
            case NOT_FOUND -> io.grpc.Status.NOT_FOUND;
            case CONFLICT -> io.grpc.Status.ALREADY_EXISTS;
            case FORBIDDEN -> io.grpc.Status.PERMISSION_DENIED;
            case NOT_IMPLEMENTED -> io.grpc.Status.UNIMPLEMENTED;
            case BAD_REQUEST -> io.grpc.Status.INVALID_ARGUMENT;
            default -> io.grpc.Status.INTERNAL;
        };

        return status.asException();
    }
}
