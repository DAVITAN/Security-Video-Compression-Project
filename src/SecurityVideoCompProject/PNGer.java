package SecurityVideoCompProject;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import org.opencv.core.Mat;

public class PNGer extends Object {
	protected static final byte IHDR[] = { 73, 72, 68, 82 };// "IHDR"
	protected static final byte IDAT[] = { 73, 68, 65, 84 };// "IDAT"
	protected static final byte IEND[] = { 73, 69, 78, 68 };// "IEND"
	protected byte[] pngArray;
	protected int scanLineFilter = 0;
	protected int compressionLevel;
	protected int writePos;
	protected int width;
	protected int height;
	protected Mat frame;
	byte[] pixdat ;

	/* Constructor */
	public PNGer(Mat _frame, int compLevel) {
		frame = _frame;
		compressionLevel = compLevel;
		width = _frame.width();
		height = _frame.height();
		pixdat = new byte[(int) (frame.total() * frame.channels())];
	}

	/*
	 * Write 1 byte to PNGArray at a certain point
	 * 
	 * @param 1: data to write
	 * 
	 * @param 2: point to start writing
	 * 
	 * @return new end of PNGArray data
	 */
	protected int writeByte(int data, int offset) {
		byte[] temp = { (byte) data };
		return writeBytes(temp, offset);
	}

	/*
	 * Write data bytes to PNGArray at a certain point
	 * 
	 * @param 1: data to write
	 * 
	 * @param 2: point to start writing
	 * 
	 * @return new end of PNGArray data
	 */
	protected int writeBytes(byte[] data, int offset) {
		if (pngArray.length < data.length + offset)
			pngArray = resizeArray(pngArray, data.length + offset);
		System.arraycopy(data, 0, pngArray, offset, data.length);
		return data.length + offset;
	}

	/*
	 * @Enlarge pngArray
	 * 
	 * @param 1: original pngArray
	 * 
	 * @param 2: new size of pngArray
	 * 
	 * @return enlarged array
	 */
	protected byte[] resizeArray(byte[] origArray, int newSize) {
		byte[] temp = new byte[newSize];
		int oldLen=origArray.length;
		System.arraycopy(origArray, 0, temp, 0, Math.min(oldLen, newSize));
		return temp;
	}

	/*
	 * Convert int (32 bits) to 4 bytes array by bitshifting and masking
	 */
	protected byte[] int2byte(int n) {
		byte[] arr = { (byte) (n >> 24), (byte) ((n >> 16) & 0xff), (byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return arr;
	}

	/*
	 * Write PNG header - {4b size of header,IHDR,4b width, 4b height, 1b:
	 * bitDepth,model,comp.,filter,interlace}
	 */
	protected int writeHeader() {
		int startPos;

		startPos = writePos = writeBytes(int2byte(13), writePos);
		writePos = writeBytes(IHDR, writePos);

		writePos = writeBytes(int2byte(width), writePos);
		writePos = writeBytes(int2byte(height), writePos);
		writePos = writeByte(8, writePos); // bit depth
		writePos = writeByte(2, writePos); // direct model
		writePos = writeByte(0, writePos); // compression method
		writePos = writeByte(0, writePos); // filter method
		writePos = writeByte(0, writePos); // no interlace

		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(pngArray, startPos, writePos - startPos);
		int crcValue = (int) crc.getValue();
		writePos = writeBytes(int2byte(crcValue), writePos);

		return writePos;
	}

	protected void filterNone(byte[] scanLine) {

	}

	/*
	 * Filter based on diff with previous pixel. Copies pixel in start of
	 * scanline as-is (prev Pixel=0)
	 * 
	 * @param scanLines: 1 or more lines of bytes (each line length=width*3 (3b
	 * per pixel)
	 * 
	 * @return resulting line of filtered values
	 */
	protected byte[] filterSub(byte[] scanLine) {
		byte[] temp = new byte[scanLine.length];

		for (int i = 0; i < scanLine.length; i++) {
			if (i % (width * 3) == 0) {
				temp[i] = scanLine[i++];
				temp[i] = scanLine[i++];
				temp[i] = scanLine[i];
			} else
				temp[i] = (byte) (scanLine[i] - scanLine[i - 3]);
		}
		return temp;
	}

	/*
	 * Filter based on diff with pixel above. Copies pixels in prior row as-is.
	 * 
	 * @param scanLines: 1 or more lines of bytes (line length=width*3 (3b per
	 * pixel)
	 * 
	 * @return resulting lines of filtered values (byte array)
	 */
	protected byte[] filterUp(byte[] scanLine,int line) {
		byte[] temp = new byte[scanLine.length];
		for (int i = 0; i < scanLine.length; i++) {
				temp[i] = (byte) ((scanLine[i] - pixdat[(line-1)*width*3+i]));
		}
		return temp;
	}

	/*
	 * Filter based on diff with AVG of previous & above pixels. First pixel in
	 * each line, and the first line, are copied as is.
	 * pixdat[] = the original img byte array
	 * @param1: scanLines: 1 line of bytes (line length=width*3 (3b per
	 * pixel)
	 * @param2: line: current line in original image data
	 * @return resulting lines of filtered values (byte array)
	 */
	protected byte[] filterAVG(byte[] scanLine,int line) {
		byte[] temp = new byte[scanLine.length];
		temp[0] = scanLine[0];
		temp[1] = scanLine[1];
		temp[2] = scanLine[2];
		double avg;
		for (int i = 3; i < scanLine.length; i++) {
			avg=(Math.floor((scanLine[i - 3] + pixdat[(line-1)*width*3+i])/2));
			temp[i] = (byte) (scanLine[i] - avg);
				
		}

		return temp;
	}

	/*
	 * Filter based on paeth linear equation; First pixel in each line, and the
	 * first line, are copied as is. if a=prev pixel, b= pixel above, c = pixel
	 * above and to the left then: p=a+b-c. answer=the smallest diff between
	 * pa,pb,pc.
	 * 
	 * @param scanLines: 1 or more lines of bytes (line length=width*3 (3b per
	 * pixel)
	 * @param2: line: current line in original image data
	 * @return resulting lines of filtered values (byte array)
	 */
	protected byte[] filterPaeth(byte[] scanLine,int line) {
		byte[] temp = new byte[scanLine.length];
		temp[0]=scanLine[0];
		temp[1]=scanLine[1];
		temp[2]=scanLine[2];
		for (int i = 3; i < scanLine.length; i++) {
				temp[i]=(byte) ((scanLine[i]&0xff-paethPredictor(scanLine, i, line)));
			}
		
		return temp;
	}
	protected int paethPredictor(byte[] temp,int i,int line){
		  		int p,pa,pb,pc;
		        int a = temp[i - 3], b = pixdat[(line-1)*width*3+i], c = pixdat[(line-1)*width*3+i - 3];
		        p =  (a + b - c);        //; initial estimate
		        pa = Math.abs(p&0xff - a&0xff);      //; distances to a, b, c
		        pb = Math.abs(p&0xff - b&0xff);
		        pc = Math.abs(p&0xff - c&0xff);
		        // return nearest of a,b,c,
		        // breaking ties in order a,b,c.
		        if (pa <= pb && pa <= pc) return a;
		        else if (pb <= pc)  return b;
		        else return c;
	}

	/*
	 * Filter selection: runs all filters for given scanline, calculates abs
	 * diff between all bytes of the resulting array. Chooses filter with
	 * minimal abs diff. Writes the filter type to scanLineFilter and returns
	 * the filtered array.
	 * 
	 * @param: 1 or more lines of bytes (line length=width*3 (3b per pixel)
	 * 
	 * @return: filtered byte array
	 */
	protected byte[] filterSelector(byte[] scanLines,int line) {
		int minAbsDiff = 0, tempAbsDiff = 0;
		byte[] temp = new byte[scanLines.length];
		byte[] minDiffArray = new byte[scanLines.length];
		//TODO put option for default filter (saves time)
		// None
		for (int i = 1; i < scanLines.length; i++)
			minAbsDiff+=Math.abs(scanLines[i] - scanLines[i - 1]);
		scanLineFilter = 0;
		minDiffArray=scanLines;
		
		//Sub filter - diff with prev pixel
		tempAbsDiff = 0;
		temp = filterSub(scanLines);
		for (int i = 1; i < scanLines.length; i++)
			tempAbsDiff += Math.abs(temp[i] - temp[i - 1]);
		if (tempAbsDiff < minAbsDiff) {
			scanLineFilter = 1;
			minDiffArray = temp;
			minAbsDiff=tempAbsDiff;
		}
		
		//Up Filter - diff with filter above
		tempAbsDiff = 0;
		temp = filterUp(scanLines,line);
		for (int i = 1; i < scanLines.length; i++)
			tempAbsDiff += Math.abs(temp[i] - temp[i - 1]);
		if (tempAbsDiff < minAbsDiff) {
			scanLineFilter = 2;
			minDiffArray = temp;
			minAbsDiff=tempAbsDiff;
		}
		
		//AVG filter - diff with (left and above pix)/2
		tempAbsDiff = 0;
		temp = filterAVG(scanLines,line);
		for (int i = 1; i < scanLines.length; i++)
			tempAbsDiff+= Math.abs(temp[i] - temp[i - 1]);
		if (tempAbsDiff < minAbsDiff) {
			scanLineFilter = 3;
			minDiffArray = temp;
			minAbsDiff=tempAbsDiff;
		}
		
		//PAETH filter - using paeth algorithm
		tempAbsDiff = 0;
		temp = filterPaeth(scanLines,line);
		for (int i = 1; i < scanLines.length; i++)
			tempAbsDiff+= Math.abs(temp[i] - temp[i - 1]);
		
		if (tempAbsDiff < minAbsDiff) {
			scanLineFilter = 4;
			minDiffArray = temp;
			minAbsDiff=tempAbsDiff;
		}
		return minDiffArray;

	}


	/*
	 * Takes chuncks of lines of pixels (32KB each), filters them, writes them in the format:
	 * <filterType(0-4)> <filteredLine>  - for each line of pixels! - for all of the image,
	 * and compresses them using deflater.
	 * Writes 1 IDAT chunck
	 */
	protected int writeIDAT(int width, int height) {
		int scanPos = 0,  origLinesLeft = height;
		Deflater compresser = new Deflater(compressionLevel);
		int lineLen=width*3,line=0;
		int toCompPos=0;
		//LINE FILTERING
		byte[] toCompress=new byte[height*lineLen*3+lineLen];
		while(origLinesLeft > 0){
			byte[] toFilter=new byte[lineLen];
			
			System.arraycopy(pixdat, scanPos, toFilter, 0, lineLen);
			scanPos+=lineLen;
			if(origLinesLeft<height){
				toFilter=filterSelector(toFilter,line);
			}
			else {
				scanLineFilter=0;
			}
			toCompress[toCompPos++]=(byte) scanLineFilter;
			System.arraycopy(toFilter, 0, toCompress, toCompPos, lineLen);
			toCompPos+=lineLen;
			line++;
			origLinesLeft--;
			
		}
		compresser.setInput(toCompress);
		compresser.finish();

		byte[] done = new byte[width * height * 3];
		int compressedDataLength = compresser.deflate(done);//Save compressed data, return amount of compressed data to the int
		done = resizeArray(done, compressedDataLength);//fit the resized data
		int blockCounter=0,donePos=0,bytesleft=0,bToCopy=0;
		int IDATBlockSize=32768;
		byte [] toIDAT = new byte[IDATBlockSize];
		while(blockCounter*IDATBlockSize<done.length){
			bytesleft=done.length-blockCounter*IDATBlockSize;
			if(bytesleft>IDATBlockSize){
				System.arraycopy(done, donePos, toIDAT, 0,IDATBlockSize);
				donePos+=IDATBlockSize;}
			else{
				bToCopy=bytesleft;
				System.arraycopy(done, donePos, toIDAT, 0,bToCopy);
				toIDAT=resizeArray(toIDAT, bToCopy);
				donePos+=bToCopy;
			}
			
			int IDATLen = toIDAT.length;
			CRC32 crc = new CRC32();
			crc.reset();

			writePos = writeBytes(int2byte(toIDAT.length), writePos);
			writePos = writeBytes(IDAT, writePos);
			crc.update(IDAT);
			writePos = writeBytes(toIDAT, writePos);

			crc.update(toIDAT, 0, IDATLen);
			int crcValue = (int) crc.getValue();
			writePos = writeBytes(int2byte(crcValue), writePos);
			blockCounter++;
		}
		

		compresser.end();

		return writePos;
		}
	
	protected int writeIEND(){
		writePos=writeBytes(int2byte(0),writePos);
		writePos=writeBytes(IEND,writePos);
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(IEND);
		int crcValue=(int) crc.getValue();
		writePos = writeBytes(int2byte(crcValue),writePos);
		return writePos;
	}

	public byte[] EncodeFrame() {
		int width = frame.width();// height - pixels
		int height = frame.height();// width - pixels
		pngArray = new byte[(width * height * 3) + 200];
		
		frame.get(0, 0, pixdat);
		//change colors from BGR to RGB
		for(int i=0;i<pixdat.length-3;i=i+3){
			byte temp = pixdat[i];
			pixdat[i]=pixdat[i+2];
			pixdat[i+2]=temp;
		}
		byte[] PNGID = { -119, 80, 78, 71, 13, 10, 26, 10 };
		writePos = writeBytes(PNGID, 0);
		writePos = writeHeader();
		writePos = writeIDAT(width, height);
		writePos = writeIEND();
		pngArray=resizeArray(pngArray, writePos);
		return pngArray;
	}
}	
		




