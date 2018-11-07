# VideoCompression(JAVA)
This is a college assignment in the subject of Data Compression.
Uses OpenCV & JCodec.
This software takes an input video from a security camera (which naturally contains a lot of static frames) and compresses it using a type of RLE algorithm combined with a self-built PNG encoder. 
The main concept:
Compression-  Read each frame, detect movement by comparison to the last frame. Compress every frame to PNG and save to file as byte array.
For frames where change wasnt detected - count the number of static frames and write to file as a byte (e.g. if only the first 5 frames out of 30 contained movement, the compressed file will be 5x<PNG Frames> + <'25'>(1 byte) ) . 

Decompress: read the byte array, decompress every PNG frame to a mat object, clone it (if needed) and create a video from the collection of decompressed frames.
