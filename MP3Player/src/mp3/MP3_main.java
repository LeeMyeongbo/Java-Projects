package mp3;

import javafx.embed.swing.*;

public class MP3_main {
	public static void main(String[] args) {
		new JFXPanel(); // MediaŬ������ ���� javafx�� ���� ���� Ư���� �ʱ�ȭ ������ ���ľ� �ϴµ� �� JFXPanel�� �� �ʱ�ȭ�� ����
		new MP3_play(); // MP3_playŬ���� ���� (���� ���� ���� �ٷ� heap ������ Ŭ���� ���� ����)
	}
}
