package SecurityVideoCompProject;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.opencv.core.Mat;

public class VidDecompressor {
	protected int fps;
	protected int readPos = 0;
	File file = null;
	File output=null;
	private static BufferedImage matToBufferedImage(Mat frame)
	{
		int type = 0;
		if (frame.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (frame.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		frame.get(0, 0, data);

		return image;
	}
	




	protected BufferedImage mat2bimg(Mat mat){
		
		int rows=mat.width(); 
		int cols=mat.height();
		BufferedImage bimg=new BufferedImage(cols, rows, BufferedImage.TYPE_3BYTE_BGR);
		long elem=mat.elemSize();
		byte[] data = new byte[(int) (rows*cols*elem)];
		mat.get(0, 0,data);
		bimg.getRaster().setDataElements(0, 0, cols, rows, data);
		return bimg;
	}
	protected int bytes2Int(byte[] intBytes) {
		ByteBuffer bb = ByteBuffer.wrap(intBytes);
		return bb.getInt();
	}

	public VidDecompressor(String _file,String _output) {
		file = new File(_file);
		output=new File(_output);
	}

	public void decompress() {
		String workingDir = System.getProperty("user.dir");
		String pathToOpenCVLib=workingDir + "/resources/openCVLib/opencv_java342.dll";
		System.load(pathToOpenCVLib);
		int nextDataSize = 0, timesToShow = 0, bytesRead = 0;
		@SuppressWarnings("unused")
		double frames,fHeight;
		byte[] fData;
		byte[] nextDataSizeB = new byte[4];
		byte[] timesToShowB = new byte[4];
		Mat frame = null;
		PNG_Decompresser unPNG;
		


		try {
			System.out.println("decompressing..");
			RandomAccessFile data = new RandomAccessFile(file, "r");
			
			int len = (int) file.length();
			fps=data.readByte();
			String outFilename = output.getPath()+"/"+file.getName().substring(0, file.getName().length()-12) + "_Decoded.mp4";
			AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(new File(outFilename), fps);
			
			bytesRead++;
			frames=data.readInt();
			bytesRead+=4;
			fHeight=data.readInt();
			bytesRead+=4;
			
			int counter=0;
			BufferedImage bimg;
			
			while (bytesRead < len) {
				counter++;
				data.readFully(nextDataSizeB);//Read nextDataSizeB.length bytes into nextDataSizeB
				bytesRead += 4;
				nextDataSize = bytes2Int(nextDataSizeB);

				fData = new byte[nextDataSize];
				data.readFully(fData);
				bytesRead += nextDataSize;
				data.readFully(timesToShowB);
				bytesRead += 4;
				timesToShow = bytes2Int(timesToShowB);
				
				unPNG=new PNG_Decompresser(fData);
				frame=unPNG.decompress();
				bimg=matToBufferedImage(frame);
				for(int i=0;i<timesToShow;i++){
				System.out.println("writing frame: " + (counter) + " time: " +i);
					enc.encodeImage(bimg);
				}

			}
			enc.finish();
			data.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
