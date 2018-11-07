package SecurityVideoCompProject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class UnFiltered 
{
	/*
	 * @param width the amount of pixels in a row
	 * @param height the amount of pixels in a column
	 * each row is encoded with a different filter
	 * the first 4 byte in a row are the filter
	 * @return a Mat with the picture's data 
	 */
	public static Mat unfilter (byte [] data,int height, int width) 
	{
		byte arr [] [] = new byte [height] [width*3];
		int current_column,k = 0;
		for (int j = 0; j < height; j ++)
		{
			int filter = data[k++];
			switch (filter)
			{
			
			case 0: 
				//none
				//System.arraycopy(data, k, arr[j], 0, (width*3));
				//k += width*3;
				for (int i = 0; i < width*3; i++,k++)
					arr[j][i] = data[k];
				break;

			case 1:
				//sub
				for (int i = 0; i < width*3; i++)
				{
					if (i < 3)
						arr[j][i] = data[k++];
					else
						arr[j][i] = (byte) (arr[j][i-3]+data[k++]);
				}
				break;

			case 2:
				//up
				for (int i = 0; i < width*3; i++)
				{
					if (j == 0)
						arr[j][i] = data[k++];
					else
						arr[j][i] = (byte) (arr[j-1][i] + data[k++]);
				}
				break;

			case 3:
				//average
				for (int i = 0; i < width*3; i++)
				{
					if (j == 0 || i <3)
						arr[j][i] = data[k++];

					else
						arr[j][i] = (byte) (data[k++] + ((arr[j][i-3] + arr[j-1][i])/2));
				}
				break;

			case 4:
				//paeth
				for (int i = 0; i < width*3; i++)
				{
					if (j == 0 || i <3)
						arr[j][i] = data[k++];

					else
						arr[j][i] = (byte) (data[k] + PaethPredictor (arr[j][i-3], arr[j-1][i], (byte)data[k++]));
				}
				break;
			}//switch-case
		}//for

		//convert the data to a Mat object
		Mat mat = new Mat(height,width,CvType.CV_8UC3);
		for (int i = 0; i < height; i++)
		{
			current_column = 0;
			for (int j = 0; j < width*3;j += 3)
			{
				byte px [] = {arr[i][j+2],arr[i][j+1],arr[i][j]};
				mat.put(i, current_column++, px);
			}
		}
		return mat;		
	}
	
	
	//this function was taken from https://www.w3.org/TR/PNG-Filters.html
	private static byte PaethPredictor(byte a, byte b, byte c)
	{
		int p = a + b - c;
		int pa = Math.abs(p - a);
		int pb = Math.abs(p - b);
		int pc = Math.abs(p - c);
		if (pa <= pb && pa <= pc) return a;
		else if (pb <= pc) return b;
		return c;
	}
}
