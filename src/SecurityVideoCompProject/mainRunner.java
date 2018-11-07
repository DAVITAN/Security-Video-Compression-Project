package SecurityVideoCompProject;
public class mainRunner {
/** ================================================================================
 * Data Compression - Final Project
 * 
 * Compresses video using "RLE" for images - only the first frame and 
 * frames that contain movement are encoded (using a PNG encoder implemented by us), 
 * so that the compressed file comprises of:(<imgArrSize(int)><imgDataArr(byte[])><timesToShow(int)>)* # of frames with movement.
 * 
 * PNG encoder implemented based on libPNG specs (http://www.libpng.org/pub/png/spec/1.2/PNG-Contents.html)
 * Movement detection is done using OpenCV for Java (https://opencv.org/releases.html).
 * ...............................................
 * Submitted by: 
 * @author Dor Avitan; @author Omer Sirpad
 * ================================================================================
 */
	public static void main(String[] args) {
		long sTime = System.currentTimeMillis();
		String toCompress = "testmov.mp4";
		VidCompressor compressor = new VidCompressor(toCompress,"", 0, "");
		
		compressor.compress();
		VidDecompressor deComp = new VidDecompressor("","");
		long mTime = System.currentTimeMillis();
		System.out.println("Compressed  in: " + (double) (mTime - sTime) / 1000 + " s");


		
		//VidDecompressor deComp = new VidDecompressor("multiPNGs.odo");
		deComp.decompress();
		long eTime = System.currentTimeMillis();
		System.out.println("Compressed & decompressed in: " + (double) (eTime - sTime) / 1000 + " s");
	}

}
