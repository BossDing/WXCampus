package com.wxcampus.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 图片辅助处理类。提供图片写入与压缩的方法。
 */
public class ToDealImage {

	/**
	 * 将image对象写入指定路径的文件
	 * 
	 * @param image
	 *            要写入的图片对象
	 * @param filepath
	 *            文件的绝对路径名（必须有后缀名，只支持png/jpg/gif的格式化方式）
	 * @return 成功返回true，失败返回false。
	 */
	public static boolean writeImageToFile(Image image, String filepath) {
		int end_index = filepath.lastIndexOf(".") + 1;
		if (end_index == 0)
			return false;// 没有后缀名直接返回失败
		String end_name = filepath.substring(end_index, filepath.length());
		try {
			BufferedImage buff_image = null;
			if (end_name.equals("png")) {
				// 根据图片创建BufferedImage对象
				buff_image = new BufferedImage(image.getWidth(null),
						image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			} else {// 单独处理防止色彩失真（否则有红色层）
				// 根据图片创建BufferedImage对象
				buff_image = new BufferedImage(image.getWidth(null),
						image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			}
			// 将图片载入到BufferedImage
			Graphics2D g2 = (Graphics2D) buff_image.getGraphics();
			g2.drawImage(image, 0, 0, null);// 这个必须有
			// 将BufferedImage对象写入本地文件
			ImageIO.write(buff_image, end_name, new File(filepath));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 压缩图片至200*200
	 * 
	 * @param oldPath
	 *            旧图片本地路径
	 * @param newPath
	 *            新图片本地路径（如果后缀名与原图不同，将以新的后缀名编码格式保存）
	 * @return 是否成功
	 */
	public static boolean zipTo200(String oldPath, String newPath) {
		// 指定大小
		int edge = 200;
		// 获取图片
		ImageIcon image_icon = new ImageIcon(oldPath);
		// 压缩图片
		Image image = ZipImage(image_icon.getImage(), edge, edge);
		// 写入本地路径
		return writeImageToFile(image, newPath);
	}

	/**
	 * 保持宽高比压缩图片，使之恰好嵌入到边界矩形<br>
	 * （转换参考：Image=ImageIcon.getImage,ImageIcon=new ImageIcon(Image)。）
	 * 
	 * @param image
	 *            要压缩的图片对象
	 * @param rect_width
	 *            边界矩形的宽
	 * @param rect_hight
	 *            边界矩形的高
	 * @return 压缩后的图片
	 */
	public static Image ZipImage(Image image_input, int rect_width,
			int rect_hight) {
		ImageIcon image = new ImageIcon(image_input);
		// 如果图片宽度大于rect_width，保持宽高比压缩宽至rect_width
		if (image.getIconWidth() > rect_width) {
			int image_hight = (int) (image.getIconHeight() * ((double) rect_width / image
					.getIconWidth()));
			image.setImage(image.getImage().getScaledInstance(rect_width,
					image_hight, Image.SCALE_DEFAULT));
		}
		// 如果压缩后图片高度超过rect_hight，保持宽高比将高度压缩至rect_hight（此时宽一定小于rect_hight）
		if (image.getIconHeight() > rect_hight) {
			int image_hight = (int) (image.getIconWidth() * ((double) rect_hight / image
					.getIconHeight()));
			image.setImage(image.getImage().getScaledInstance(image_hight,
					rect_hight, Image.SCALE_DEFAULT));
		}
		return image.getImage();
	}

}
