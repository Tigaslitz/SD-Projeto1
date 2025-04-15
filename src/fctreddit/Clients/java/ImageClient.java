package lab4.clients.java;

import lab4.api.java.Image;
import lab4.api.java.Result;

public abstract class ImageClient implements Image {
	
	protected static final int READ_TIMEOUT = 5000;
	protected static final int CONNECT_TIMEOUT = 5000;

	protected static final int MAX_RETRIES = 10;
	protected static final int RETRY_SLEEP = 5000;
	
	abstract public Result<String> createImage(byte[] imageContents);;

	abstract public Result<byte[]> getImage(String imageId);
	
	abstract public Result<Void> deleteImage(String imageId);

}
