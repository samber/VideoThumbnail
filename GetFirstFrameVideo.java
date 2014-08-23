package utils;

/*******************************************************************************
 * Copyright (c) 2008, 2010 Xuggle Inc.  All rights reserved.
 *  
 * This file is part of Xuggle-Xuggler-Main.
 *
 * Xuggle-Xuggler-Main is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Xuggle-Xuggler-Main is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Xuggle-Xuggler-Main.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

import javax.imageio.ImageIO;

import java.io.File;

import java.awt.image.BufferedImage;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

/**
 * Using {@link IMediaReader}, takes a media container, finds the first video stream, decodes that
 * stream, and then writes video frames out to a PNG image file every 5
 * seconds, based on the video presentation timestamps.
 *
 * @author aclarke
 * @author trebor
 */

public class GetFirstFrameVideo extends MediaListenerAdapter {

    public String thumbnail_path = null;

    /** Construct a GetFirstFrameVideo which reads and captures
     * frames from a video file.
     *
     * @param filename the name of the media file to read
    */
    public GetFirstFrameVideo(String path) {
        // create a media reader for processing video
        IMediaReader reader = ToolFactory.makeReader(path);

        // stipulate that we want BufferedImages created in BGR 24bit color space
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

        // note that GetFirstFrameVideo is derived from
        // MediaReader.ListenerAdapter and thus may be added as a listener
        // to the MediaReader. GetFirstFrameVideo implements
        // onVideoPicture().
        reader.addListener(this);

        // read out the contents of the media file, note that nothing else
        // happens here.  action happens in the onVideoPicture() method
        // which is called when complete video pictures are extracted from
        // the media source

//        while (reader.readPacket() == null)
//            do {} while(false);

        // il faut lire un certain nombre de packet pour être certain de passer la première frame
        // 1000 packets est un nombre arbitraire, mais utile pour éviter de lire un fichier de 2Go jusqu'à la fin...
        for (int i = 0; i < 1000 && thumbnail_path == null; i++)
            reader.readPacket();
        reader.close();
    }

  /** 
   * Called after a video frame has been decoded from a media stream.
   * Optionally a BufferedImage version of the frame may be passed
   * if the calling {@link IMediaReader} instance was configured to
   * create BufferedImages.
   * 
   * This method blocks, so return quickly.
   */
    public void onVideoPicture(IVideoPictureEvent event)
    {
        try {
            File file = File.createTempFile("frame", ".png");
            thumbnail_path = file.getAbsolutePath();
            ImageIO.write(event.getImage(), "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
