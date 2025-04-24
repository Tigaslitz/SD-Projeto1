package fctreddit.Clients.grpc;

import com.google.protobuf.ByteString;
import fctreddit.Clients.java.ImageClient;
import fctreddit.Services.ImageService.grpc.generated_java.ImageGrpc;
import fctreddit.Services.ImageService.grpc.generated_java.ImageProtoBuf;
import fctreddit.Services.UserService.grpc.generated_java.UsersGrpc;
import fctreddit.Services.UserService.grpc.generated_java.UsersProtoBuf;
import fctreddit.Services.grpc.util.DataModelAdaptor;
import fctreddit.api.Interfaces.Result;
import io.grpc.Channel;
import io.grpc.LoadBalancerRegistry;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.PickFirstLoadBalancerProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static fctreddit.Clients.grpc.GrpcUsersClient.statusToErrorCode;

public class GrpcImageClient extends ImageClient {

    static {
        LoadBalancerRegistry.getDefaultRegistry().register(new PickFirstLoadBalancerProvider());
    }

    final ImageGrpc.ImageBlockingStub stub;

    public GrpcImageClient(URI serverURI) {
        Channel channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort()).usePlaintext().build();
        stub = ImageGrpc.newBlockingStub( channel ).withDeadlineAfter(READ_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Override
    public Result<String> createImage(String userId, byte[] imageContents, String password) {
        try {
            ImageProtoBuf.CreateImageResult res = stub.createImage(ImageProtoBuf.CreateImageArgs.newBuilder()
                    .setUserId(userId)
                    .setImageContents(ByteString.copyFrom(imageContents))
                    .setPassword(password)
                    .build());

            return Result.ok(res.getImageId());
        } catch (StatusRuntimeException sre) {
            return Result.error( statusToErrorCode(sre.getStatus()));
        }
    }

    @Override
    public Result<byte[]> getImage(String userId, String imageId) {
        try {
            Iterator<ImageProtoBuf.GetImageResult> resIterator = stub.getImage(ImageProtoBuf.GetImageArgs.newBuilder()
                    .setUserId(userId)
                    .setImageId(imageId)
                    .build());

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            while (resIterator.hasNext()) {
                ImageProtoBuf.GetImageResult chunk = resIterator.next();
                output.write(chunk.getData().toByteArray());
            }

            return Result.ok(output.toByteArray());
        } catch (StatusRuntimeException sre) {
            return Result.error( statusToErrorCode(sre.getStatus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<Void> deleteImage(String userId, String imageId, String password) {
        try {
            ImageProtoBuf.DeleteImageResult res = stub.deleteImage(ImageProtoBuf.DeleteImageArgs.newBuilder()
                    .setUserId(userId)
                    .setImageId(imageId)
                    .setPassword(password)
                    .build());

            return Result.ok();

        } catch (StatusRuntimeException sre) {
            return Result.error( statusToErrorCode(sre.getStatus()));
        }
    }
}
