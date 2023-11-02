package com.kitty.radar.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class BZip2 {

	public static boolean extract(String fromFile, String toFile) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(fromFile)));
			os = new BufferedOutputStream(new FileOutputStream(toFile));
			IOUtils.copy(is, os);
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
		}
		return true;
	}

}
