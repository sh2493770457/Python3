import cv2
import mediapipe as mp
from PIL import Image, ImageDraw, ImageFont
import numpy as np

# 初始化MediaPipe手部模型
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils


def detect_hand_gesture(hand_landmarks):
    thumb_tip = hand_landmarks.landmark[mp_hands.HandLandmark.THUMB_TIP]
    index_finger_tip = hand_landmarks.landmark[mp_hands.HandLandmark.INDEX_FINGER_TIP]
    middle_finger_tip = hand_landmarks.landmark[mp_hands.HandLandmark.MIDDLE_FINGER_TIP]
    ring_finger_tip = hand_landmarks.landmark[mp_hands.HandLandmark.RING_FINGER_TIP]
    pinky_tip = hand_landmarks.landmark[mp_hands.HandLandmark.PINKY_TIP]

    if thumb_tip.y < index_finger_tip.y < middle_finger_tip.y < ring_finger_tip.y < pinky_tip.y:
        return "手指张开"
    else:
        return "手势未识别"


def draw_chinese_text(image, text, position, font_size=20):
    pil_image = Image.fromarray(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    draw = ImageDraw.Draw(pil_image)
    font = ImageFont.truetype("simsun.ttc", font_size, encoding="utf-8")
    draw.text(position, text, (0, 255, 0), font=font)
    return cv2.cvtColor(np.array(pil_image), cv2.COLOR_RGB2BGR)


def main():
    hands = mp_hands.Hands(max_num_hands=1, min_detection_confidence=0.7)
    cap = cv2.VideoCapture(0)

    if not cap.isOpened():
        print("无法打开相机")
        return

    while True:
        ret, frame = cap.read()
        if not ret:
            print("无法获取视频帧")
            break

        frame = cv2.flip(frame, 1)
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

        result = hands.process(rgb_frame)

        if result.multi_hand_landmarks:
            for hand_landmarks in result.multi_hand_landmarks:
                mp_drawing.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)
                gesture = detect_hand_gesture(hand_landmarks)
                frame = draw_chinese_text(frame, gesture, (10, 50), 30)

        cv2.imshow('hand', frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    hands.close()
    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()

import cv2
import mediapipe as mp
from PIL import Image, ImageDraw, ImageFont
import numpy as np
import subprocess

# 初始化MediaPipe手部模型
# mp_hands = mp.solutions.hands
# mp_drawing = mp.solutions.drawing_utils
#
# # 初始化一个变量用于存储上一次的手势ID
# last_gesture_id = 0
#
#
# def detect_hand_gesture(hand_landmarks):
#     fingers = []
#
#     thumb_tip = hand_landmarks.landmark[mp_hands.HandLandmark.THUMB_TIP]
#     index_finger_tip = hand_landmarks.landmark[mp_hands.HandLandmark.INDEX_FINGER_TIP]
#     middle_finger_tip = hand_landmarks.landmark[mp_hands.HandLandmark.MIDDLE_FINGER_TIP]
#     ring_finger_tip = hand_landmarks.landmark[mp_hands.HandLandmark.RING_FINGER_TIP]
#     pinky_tip = hand_landmarks.landmark[mp_hands.HandLandmark.PINKY_TIP]
#
#     fingers.append(thumb_tip.y < hand_landmarks.landmark[mp_hands.HandLandmark.THUMB_IP].y)
#     fingers.append(index_finger_tip.y < hand_landmarks.landmark[mp_hands.HandLandmark.INDEX_FINGER_DIP].y)
#     fingers.append(middle_finger_tip.y < hand_landmarks.landmark[mp_hands.HandLandmark.MIDDLE_FINGER_DIP].y)
#     fingers.append(ring_finger_tip.y < hand_landmarks.landmark[mp_hands.HandLandmark.RING_FINGER_DIP].y)
#     fingers.append(pinky_tip.y < hand_landmarks.landmark[mp_hands.HandLandmark.PINKY_DIP].y)
#
#     count = fingers.count(True)
#
#     if count == 1:
#         return "一根手指", 1
#     elif count == 2:
#         return "两根手指", 2
#     elif count == 3:
#         return "三根手指", 3
#     elif count == 4:
#         return "四根手指", 4
#     elif count == 5:
#         return "五根手指", 5
#     else:
#         return "手势未识别", 0
#
#
# def draw_chinese_text(image, text, position, font_size=20):
#     pil_image = Image.fromarray(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
#     draw = ImageDraw.Draw(pil_image)
#     font = ImageFont.truetype("simsun.ttc", font_size, encoding="utf-8")
#     draw.text(position, text, (0, 255, 0), font=font)
#     return cv2.cvtColor(np.array(pil_image), cv2.COLOR_RGB2BGR)
#
#
# def perform_action(gesture_id):
#     try:
#         if gesture_id == 1:
#             # 打开 QQ 音乐
#             subprocess.run(['cmd', '/c', 'start', 'E:\\QQ音乐\\QQMusic\\QQMusic.exe'], check=True)
#         elif gesture_id == 2:
#             # 打开 微信
#             subprocess.run(['cmd', '/c', 'start', 'E:\\微信\\WeChat\\WeChat.exe'], check=True)
#
#         elif gesture_id == 3:
#             # 其他动作
#             subprocess.run(['cmd', '/c', 'start', 'E:\\QQ\\QQ.exe'], check=True)
#         elif gesture_id == 4:
#             # 其他动作
#             print("四根手指 - 自定义动作")
#         elif gesture_id == 5:
#             # 其他动作
#             print("五根手指 - 自定义动作")
#     except subprocess.CalledProcessError as e:
#         print(f"动作执行失败: {e}")
#
#
# def main():
#     global last_gesture_id  # 使用全局变量追踪上一次的手势ID
#     hands = mp_hands.Hands(max_num_hands=1, min_detection_confidence=0.7)
#     cap = cv2.VideoCapture(0)
#
#     if not cap.isOpened():
#         print("无法打开相机")
#         return
#
#     while True:
#         ret, frame = cap.read()
#         if not ret:
#             print("无法获取视频帧")
#             break
#
#         frame = cv2.flip(frame, 1)
#         rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
#
#         result = hands.process(rgb_frame)
#
#         if result.multi_hand_landmarks:
#             for hand_landmarks in result.multi_hand_landmarks:
#                 mp_drawing.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)
#                 gesture, gesture_id = detect_hand_gesture(hand_landmarks)
#                 frame = draw_chinese_text(frame, gesture, (10, 50), 30)
#
#                 if gesture_id != last_gesture_id:  # 仅在手势改变时执行动作
#                     perform_action(gesture_id)
#                     last_gesture_id = gesture_id  # 更新上一次手势ID
#
#         cv2.imshow('hand', frame)
#
#         if cv2.waitKey(1) & 0xFF == ord('q'):
#             break
#
#     hands.close()
#     cap.release()
#     cv2.destroyAllWindows()
#
#
# if __name__ == "__main__":
#     main()
