import cv2
from PIL import Image, ImageDraw, ImageFont
import numpy as np


def put_chinese_text(img, text, position, fontpath, fontsize, color):
    # 转换OpenCV图像到PIL图像
    img_pil = Image.fromarray(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    draw = ImageDraw.Draw(img_pil)
    font = ImageFont.truetype(fontpath, fontsize, encoding="utf-8")

    # 绘制文本
    draw.text(position, text, font=font, fill=color)

    # 转换回OpenCV图像
    img = cv2.cvtColor(np.array(img_pil), cv2.COLOR_RGB2BGR)
    return img


def main():
    # 加载预训练的人脸检测模型
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    # 打开摄像头
    cap = cv2.VideoCapture(0)

    if not cap.isOpened():
        print("无法打开相机")
        return

    while True:
        # 读取视频帧
        ret, frame = cap.read()

        if not ret:
            print("无法获取视频帧")
            break

        # 转换为灰度图像
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        # 检测人脸
        faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

        # 画出检测到的人脸
        for (x, y, w, h) in faces:
            cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)

        # # 在图像上添加中文文字
        # frame = put_chinese_text(frame, "人脸识别", (10, 10), "simsun.ttc", 30, (255, 0, 0))

        # 显示结果
        cv2.imshow('Face', frame)

        # 按 'q' 键退出循环
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # 释放摄像头并关闭窗口
    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()

