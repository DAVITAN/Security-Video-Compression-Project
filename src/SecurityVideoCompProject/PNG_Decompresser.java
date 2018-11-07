package SecurityVideoCompProject;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class PNG_Decompresser
{

	private ArrayList<byte []> deflated_idat_vector;
	private Mat mat;
	private byte [] compressed_data;
	protected void setData(byte[] _PNGData){
		compressed_data=_PNGData;
	}
/**
 * bytes2Int takes a 4 bytes array and translates it to an integer,
 * using the ByteBuffer class and wrap method.
 * @param intBytes : a 4 byte array
 * @return Integer value
 */
	protected int bytes2Int(byte[] intBytes) {
		ByteBuffer bb = ByteBuffer.wrap(intBytes);
		return bb.getInt();
	}
	
	public PNG_Decompresser (byte[] _PNGData)
	{
		compressed_data=_PNGData;
		deflated_idat_vector = new ArrayList<byte[]>();
	}
	
	/**
	 * The function extracts all of the IDAT blocks into an ArrayList of byte array
	 * The function ends at the end of the last IDAT
	 * @param compressed_data
	 * @param marker marks the index of reading from the compressed_data array
	
	 */
	private void extractIDAT(byte [] compressed_data, int marker)
	{
		byte [] idat_size_byte = new byte [4];
		while (true)
		{
			System.arraycopy(compressed_data, marker, idat_size_byte, 0, 4);
			marker += 8;//each block begins with the block's size and after the word "IDAT", we don't need to read it
			
			int size = bytes2Int(idat_size_byte);

			//zero means beginning of IEND
			if (size != 0)
			{
				byte [] current_idat_chunck = new byte [size];

				//Extract deflated data
				System.arraycopy(compressed_data, marker, current_idat_chunck, 0,  size);
				marker += size;

				deflated_idat_vector.add(current_idat_chunck);

				marker += 4;//skip CRC
			}
			else break;
		}
	}
	
	/**
	 * Merges all IDATs to one array and inflates
	 * @param rawDataSize will be width*height*3 + height, that's the amount of bytes in the decoded IDAT
	 * @return IDAT as one byte array
	 */
	private byte [] decodeIDAT(int rawDataSize) throws DataFormatException
	{
		Inflater inflater = new Inflater();
		//decompressing as a whole array
		byte[] fulldata=new byte[deflated_idat_vector.size()*32768];
		for (int i = 0; i < deflated_idat_vector.size(); i++){
			byte[] temp = deflated_idat_vector.get(i);
			System.arraycopy(temp, 0, fulldata, i*32768, temp.length);
		}
		
		
		
		
		
		
		byte[] inflate = new byte[rawDataSize];
		inflater.setInput(fulldata);
		inflater.inflate(inflate);
		inflater.end();

		
		
		return inflate;
	}
	
	/**
	 * converts the compressed data to a mat object
	 */
	public Mat decompress()
	{
		try
		{
			/*~~~~~~~~~~~~~~~~Initialize variables~~~~~~~~~~~~~~~~~~~~*/
			byte [] byte_width = new byte [4];
			byte [] byte_height = new byte [4];

			//get image's width from header
			System.arraycopy(compressed_data, 16, byte_width, 0, 4);

			//get image's height from header
			System.arraycopy(compressed_data, 20, byte_height, 0, 4);
			
			//convert byte arrays to int
			int image_width = bytes2Int(byte_width);
			int image_height = bytes2Int(byte_height);
			
			mat = new Mat(image_height, image_width, CvType.CV_8UC3); 
			
			int idatMarker = 33;//end of header
			/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
			
			extractIDAT(compressed_data, idatMarker);
			byte [] IDAT = decodeIDAT(image_height*image_width*3 + image_height);
			
			mat = UnFiltered.unfilter(IDAT, image_height, image_width);
		} 
		catch (DataFormatException e) {e.printStackTrace();}
		return mat;
	}
	
}
