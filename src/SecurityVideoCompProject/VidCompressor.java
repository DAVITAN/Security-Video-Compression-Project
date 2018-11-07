package SecurityVideoCompProject;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class VidCompressor {
	protected String fileName;
	protected int compressionLevel;
	protected String outFolder;
	protected String path;
	
	/**
	 * resizes an input array to an input size.
	 * If new size is smaller, will cut the data up until the new size.
	 * @param 1: original pngArray
	 * @param 2: new size of pngArray
	 * @return: resized array
	 */
	public static byte[] resizeArray(byte[] origArray, int newSize) {
		byte[] temp = new byte[newSize];
		int oldLen = origArray.length;
		System.arraycopy(origArray, 0, temp, 0, Math.min(oldLen, newSize));
		return temp;
	}

	/**
	 * Convert int (32 bits) to 4 bytes array by bitshifting and masking.
	 * @param intToConvert: integer to convert to bytes.
	 * @return array of bytes representing the integer
	 */
	public static byte[] int2byte(int intToConvert) {
		byte[] arr = { (byte) (intToConvert >> 24), (byte) ((intToConvert >> 16) & 0xff), (byte) ((intToConvert >> 8) & 0xff), (byte) (intToConvert & 0xff) };
		return arr;
	}

	/**
	 * Compressor constructor
	 * @param _file: file to compress
	 * @param _path: path of the file
	 * @param _compLvl: compression level for the deflater method (1-7)
	 */
	public VidCompressor(String _file,String _path, int _compLvl,String _outFolder) {
		fileName=_file;
		path=_path;
		compressionLevel=_compLvl;
		outFolder=_outFolder;
	}

	/**
	 * Compress video.
	 * Movement detection done by grayscaling & bluring the 2 images and finding differences between them.
	 * The differences are then being compared to a predefined threshold (to ignore noise).
	 * Images different than the previous one will be encoded along with how many frames were discarded in between.
	 * @return name of the compressed file (name of input file with "odo" extension).
	 */
	protected int compress() {
		String workingDir = System.getProperty("user.dir");
		String pathToOpenCVLib=workingDir + "/resources/openCVLib/opencv_java342.dll";
		System.load(pathToOpenCVLib);
		
		Mat frame = new Mat();
		Mat lastCoded=new Mat();
		Mat currFrame = new Mat();
		Mat prevFrame = new Mat();
		Mat gray = new Mat();
		Mat frameDelta = new Mat();
		Mat thresh = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		int currFNum, lastCodedFrame, staticFrames = 0, currFSize,totalCompressedSize = 0;
		PNGer pngEncoder;
		byte[] currFBytes;
		boolean motion = false;

		/*Open 2 output streams - fos creates the file, fosAppend appends bytes to the file.
		 * fosAppend is used to write the file gradually to prevent load on the memory.
		 */
		String outFilename = outFolder+"/"+fileName.substring(0, fileName.length()-4) + "_encoded.odo";
		
		FileOutputStream  fosAppend = null;
		
		try {
			
			fosAppend = new FileOutputStream(outFilename);
		} catch (FileNotFoundException e1) {
			System.out.println("Output file path not found !");
		}


		VideoCapture vid = new VideoCapture();
		vid.open(path);
		if (!vid.isOpened()) {
			System.out.println("Error opening video!");
			System.exit(1);
		}
		try {
			byte[] fps = { (byte) vid.get(5) };
			fosAppend.write(fps);
			totalCompressedSize++;
			
			vid.read(frame);
			byte[] frames = int2byte((int) vid.get(7));
			fosAppend.write(frames);
			totalCompressedSize+=4;
			byte[] height = int2byte(frame.height());
			fosAppend.write(height);
			totalCompressedSize+=4;
			currFNum = (int) vid.get(1) - 1;// get(1) returns # of next frame to be read
			pngEncoder = new PNGer(frame, compressionLevel);
			
			//Get and write size of and data of PNG encoding of the first frame
			currFBytes = pngEncoder.EncodeFrame();
			currFSize = currFBytes.length;
			totalCompressedSize++;
			totalCompressedSize+= currFSize;
			fosAppend.write(int2byte(currFSize));
			fosAppend.write(currFBytes);
			lastCodedFrame = currFNum;
			System.out.println("Compressing...");

			Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);// convert frame to grayscale & save as firstFrame
			Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);// apply blur (prevents noise from being interpreted as movement)
			lastCoded=gray.clone();
			while (vid.read(frame)) {
				//prevFrame = gray.clone();
				currFrame = frame.clone();
				currFNum = (int) vid.get(1) - 1;
				Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
				Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);
				
				Core.absdiff(lastCoded, gray, frameDelta);//prevFrame, gray, frameDelta);// compute difference between first frame and current frame
				Imgproc.threshold(frameDelta, thresh, 20, 255, Imgproc.THRESH_BINARY);
				Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
				Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
				
				for (int i = 0; i < contours.size(); i++) {
				// check size of areas inside of contours (marking movement) in the delta frame
					if (Imgproc.contourArea(contours.get(i)) < 4600) {
						motion = false;
					} else {
						motion = true;
					}
				}
				if (motion){
						// Found Motion
					lastCoded=gray.clone();
						staticFrames = currFNum - lastCodedFrame;
						fosAppend.write(int2byte(staticFrames)); // times to show the static frame
						totalCompressedSize++;
						//Compress & write new frame size and PNG encoding
						pngEncoder = new PNGer(currFrame, compressionLevel);
						currFBytes = pngEncoder.EncodeFrame();
						currFSize = currFBytes.length;
						totalCompressedSize++;
						totalCompressedSize+=currFSize;
						fosAppend.write(int2byte(currFSize));
						fosAppend.write(currFBytes);
						lastCodedFrame = currFNum;
				}
			}
			staticFrames = currFNum - lastCodedFrame;
			fosAppend.write(int2byte(staticFrames)); // times to show the last frame
			totalCompressedSize++;
			fosAppend.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return totalCompressedSize;
	}
}
