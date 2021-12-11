package mp3;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.mp3.*;
import org.jaudiotagger.tag.*;

class MP3_play extends JFrame implements ActionListener { // JFrame ��� �� ActionListener ����
	private static final long serialVersionUID = 1L;
	private ImageIcon image = new ImageIcon("background.png"); // ���
	private JPanel jp = new JPanel() { // �гο��� ���� ����� �׸�
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), null);
			super.paintComponents(g);
		}
	};
	private ArrayList<File> filelists = new ArrayList<File>(); // mp3 ���� ����� ArrayList
	private String[] but = { "���.png", "������.png", "������.png", "�Ͻ�����.png" }; // 4���� ��ư �̹��� ���� �̸�
	private String[] opt = {"��ü �� ����.png", "��ü�ݺ�.png", "����.png"}; // 3���� ��� �ɼ� ���� �̸�
	private ImageIcon[] buttonimgs = new ImageIcon[4]; // ���, �Ͻ�����, ������, ������ ��ư �̹���
	private ImageIcon[] optionimgs = new ImageIcon[3]; // ��ü �� �� ���, ��ü �ݺ� ���, ���� ��� ��ư �̹���
	private JButton[] jb = new JButton[3]; // ���, �Ͻ�����, ������, ������ ��ư
	private JButton[] options = new JButton[3]; // ��ü �� �� ���, ��ü �ݺ� ���, ���� ��� ��ư
	private JSlider js1 = new JSlider(JSlider.HORIZONTAL); // �뷡 ��� ���¸� ��Ÿ���� js1 �����̴�
	private JSlider js2 = new JSlider(JSlider.VERTICAL); // ���� ũ�⸦ ��Ÿ���� js2 �����̴�
	private JLabel presentimes = new JLabel(); // ���� ��� ���� �ð� ǥ��
	private JLabel lastimes = new JLabel(); // �� �뷡 ���� ǥ��
	private JLabel song = new JLabel(); // ��� ���� �뷡 ���� ǥ��
	private JLabel vol = new JLabel(); // ���� ǥ��
	private JTextField jt = new JTextField(); // �˻�â
	private JButton searchb = new JButton("�˻�"); // �˻� ��ư
	private JButton addb = new JButton("�߰�"); // �߰� ��ư(���丮���� �뷡�� PlayList�� �߰���)
	private JButton deleteb = new JButton("����"); // ���� ��ư(PlayList���� �뷡 ����)
	private JButton savelists = new JButton("��� ��������"); // ������ �������� ��ư
	private JButton openlists = new JButton("��� �ҷ�����"); // ������ �ҷ����� ��ư
	private JFileChooser chooser = new JFileChooser("C:\\"); // �߰� ��ư�� ������ �� ���� ���丮�� ��Ÿ��
	private FileNameExtensionFilter filter = new FileNameExtensionFilter(".mp3, .dat", "mp3", "dat"); // JFileChooser���� .mp3, .dat ���ϸ� ���͸�
	private DefaultListModel<String> listmodel = new DefaultListModel<String>(); // JList�� ����Ʈȭ�� �⺻ ListModel ����
	private JList<String> jl = new JList<String>(); // PlayList�� �߰��� �뷡 ��� ��Ÿ��
	private JScrollPane scroll = null; // PlayList�� ��ũ�ѹٸ� �߰�(����� �������� ��ũ�ѹٷ� ������ �ʴ� ��ϵ���� �� �� ����)
	private Media m = null; // mp3���� ������ ���� MediaŬ���� Ÿ���� ���� m
	private MediaPlayer player = null; // MediaŬ������ �ִ� mp3������ ������ �����ϱ� ���� MediaPlayer Ŭ����Ÿ���� ���� player
	private int size; // PlayList�� ����� �뷡�� ������ �ǽð����� �ľ�
	private LinkedList<Integer> order = new LinkedList<Integer>(); // ���� ����� �� ��� ������ ���ϱ� ����
	// �뷡�� ���۵Ǿ��� �� �ֱ������� ó���� �۾� ���
	private Timer t = new Timer(100, e -> { // 0.1�� ���� �ݺ��ؼ� �ش� actionPerformed�� ȣ�� (t.start() �޼ҵ� ȣ�� ����)
		Duration d1 = player.getCurrentTime();
		Duration d2 = player.getTotalDuration();
		String time1 = String.format("%02d:%02d%n", (int) d1.toMinutes(), (int) d1.toSeconds() % 60);
		String time2 = String.format("%02d:%02d%n", (int) d2.toMinutes(), (int) d2.toSeconds() % 60); 
		presentimes.setText(time1);
		lastimes.setText(time2);
		js1.setValue((int) (100 * (d1.toSeconds() / d2.toSeconds()))); 
		player.setOnEndOfMedia(() -> {
			int n = jl.getSelectedIndex();
			if(!options[0].isEnabled()) { // ��ü �� �� ����� ���
				if(n < listmodel.getSize() - 1) {
					jl.setSelectedIndex(n + 1);
					song.setText(jl.getSelectedValue());
					m = new Media(filelists.get(n + 1).toURI().toString());
					player = new MediaPlayer(m);
					player.play();
				}
			} else if(!options[1].isEnabled()) { // ��ü �ݺ� ����� ���
				if(n < listmodel.getSize() - 1) {
					jl.setSelectedIndex(n + 1);
					m = new Media(filelists.get(n + 1).toURI().toString());
				} else {
					jl.setSelectedIndex(0);
					m = new Media(filelists.get(0).toURI().toString());
				}
				player = new MediaPlayer(m);
				song.setText(jl.getSelectedValue());
				player.play();
			} else { // ���� ����� ���
				if(size != listmodel.getSize()) { // ����Ʈ ���� �뷡 ������ �ٲ���� ���
					order.clear();
					size = listmodel.getSize();
					for(int i = 0; i < size; i++)
						order.add(i);
					int present = jl.getSelectedIndex();
					order.remove(present);
					Collections.shuffle(order);
				}
				if(order.size() > 0) {
					jl.setSelectedIndex(order.getFirst());
					m = new Media(filelists.get(order.pop()).toURI().toString());
					song.setText(jl.getSelectedValue());
					player = new MediaPlayer(m);
					player.play();
				}
			}
		});
	});
	// mp3 �뷡 ��� �����̴� ����
	class Mp3Stick extends MouseAdapter implements MouseListener {
		public void modifySlide(double percent) {
			js1.setValue((int) percent);
			int pos = (int) player.getTotalDuration().toSeconds();
			player.seek(Duration.seconds(pos * (js1.getValue() / 100.0)));
			if (jb[0].getIcon().equals(buttonimgs[3])) {
				player.play();
				t.start();
			} else
				player.pause();
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			t.stop();
			player.pause();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point p = e.getPoint();
			double percent = p.getX() / (double) js1.getWidth() * 100;
			modifySlide(percent);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			Point p = e.getPoint();
			double percent = p.getX() / (double) js1.getWidth() * 100;
			modifySlide(percent);
		}
	}
	// �Ҹ� ũ�� �����̴� ����
	class VolumeStick extends MouseAdapter implements MouseListener {
		public void modifySlide(double percent) {
			js2.setValue((int)percent);
			if (player != null)
				if ((double) js2.getValue() / 100 >= 0.5) {
					player.setVolume((((double) js2.getValue() / 100) * 7.0 / 5.0) - 0.4);
				} else
					player.setVolume(((double) js2.getValue() / 100 * 2 * (double) js2.getValue() / 100 * 2) * 0.3);
			vol.setText("Vol : " + js2.getValue());
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			Point p = e.getPoint();
			double percent = (js2.getHeight() - p.getY()) / (double)js2.getHeight() * 100;
			modifySlide(percent);
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			vol.setVisible(true);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			vol.setVisible(false);
		}
	}
	// ��Ͽ� �ִ� �뷡 �������� �� �̺�Ʈ ó��
	class Listselect extends MouseAdapter implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			int n = jl.getSelectedIndex();
			deleteb.setEnabled(true);
			if(e.getClickCount() == 2) {
				js1.setVisible(true);
				t.start();
				if (m != null)
					player.stop();
				m = new Media(filelists.get(n).toURI().toString());
				player = new MediaPlayer(m);
				player.play();
				player.setVolume(0.3);
				jb[0].setIcon(buttonimgs[3]);
				song.setText(jl.getSelectedValue());
			}
		}
	}
	// Ŭ���� ������(MP3 Player �⺻ ����)
	public MP3_play() {
		super("MP3 �÷��̾�"); // ���� Ŭ������ JFrame Ŭ������ ������ ȣ��
		for (int i = 0; i < 3; i++) { // ���(i = 0), ������(i = 1), ������(i = 2)
			buttonimgs[i] = new ImageIcon(but[i]); // ������ ����� �Է�(���� �̸��� ������ ��� ��ο� ��ġ�� �ִ� ������ ����)
			jb[i] = new JButton(buttonimgs[i]); // ��ư �̹����� ������ ��ư ����
			jb[i].setBorderPainted(false); // ��ư ��輱 ����
			jb[i].setContentAreaFilled(false); // ��ư ���� �� ����
			jb[i].setFocusPainted(false); // �̹��� ��輱 ����
			jb[i].setSize(100, 100); // ��ư ũ�⸦ 100*100 ���� ����
			jb[i].addActionListener(this);
			jp.add(jb[i]);
			optionimgs[i] = new ImageIcon(opt[i]);
			options[i] = new JButton(optionimgs[i]);
			options[i].setBorderPainted(false);
			options[i].setContentAreaFilled(false);
			options[i].setFocusPainted(false);
			options[i].setSize(45, 40);
			options[i].setLocation(140 + i * 84, 170);
			options[i].addActionListener(this);
			jp.add(options[i]);
		}
		buttonimgs[3] = new ImageIcon(but[3]); // �Ͻ����� ��ư �̹����� buttonimgs�� 3�� index�� ����
		setLocation(600, 50); // (x, y) -> ���ʿ��� ���������� ������ x ����, ������ �Ʒ��� ������ y ����
		setSize(500, 700); // MP3 â ũ�⸦ 500*700���� ����
		add(jp);
		jb[0].setLocation(200, 30);
		jb[0].setToolTipText("PlayList���� ������ �뷡�� ����մϴ�.");
		jb[1].setLocation(100, 30);
		jb[1].setToolTipText("PlayList���� ������ �뷡�� �������� ����մϴ�.");
		jb[2].setLocation(292, 30);
		jb[2].setToolTipText("PlayList���� ������ �뷡�� �������� ����մϴ�.");
		options[0].setEnabled(false);
		options[0].setToolTipText("PlayList�� �ִ� ���� �� ������ ����մϴ�.");
		options[1].setToolTipText("PlayList�� �ִ� ���� �ݺ��ؼ� ����մϴ�.");
		options[2].setToolTipText("PlayList�� �ִ� ���� �������� ����մϴ�.");
		jp.add(js1);
		js1.setVisible(false);
		jp.add(js2);
		searchb.setLocation(370, 220);
		searchb.setSize(70, 30);
		searchb.addActionListener(this);
		searchb.setActionCommand("search"); // �˻� ��ư�� ������ ���� �߻��Ǵ� �̺�Ʈ �̸��� "search"�� ����
		searchb.setToolTipText("PlayList �ȿ� �ִ� �뷡�� �߿��� �˻��մϴ�.");
		jp.add(searchb);
		addb.setLocation(297, 620);
		addb.setSize(70, 30);
		addb.addActionListener(this);
		addb.setToolTipText("�뷡�� PlayList�� �߰��մϴ�.");
		jp.add(addb);
		deleteb.setLocation(373, 620);
		deleteb.setSize(70, 30);
		deleteb.setEnabled(false);
		deleteb.addActionListener(this);
		deleteb.setToolTipText("������ �뷡�� PlayList���� �����մϴ�.");
		jp.add(deleteb);
		jl.setModel(listmodel);
		jl.setFont(new Font("HY����M", Font.PLAIN, 18));
		LineBorder lborder = new LineBorder(Color.green, 4);
		TitledBorder border = new TitledBorder(lborder, "PlayLists", TitledBorder.TOP, TitledBorder.CENTER);
		border.setTitleFont(new Font("Algerian", Font.BOLD, 30));
		border.setTitleColor(Color.CYAN);
		jl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jl.setForeground(Color.GRAY);
		jl.setSelectionForeground(Color.BLACK);
		jl.addMouseListener(new Listselect());
		scroll = new JScrollPane(jl);
		scroll.setSize(395, 330);
		scroll.setLocation(50, 280);
		scroll.setBorder(border);
		scroll.setOpaque(false);
		jp.add(scroll);
		jt.setLocation(52, 220);
		jt.setSize(305, 30);
		jt.registerKeyboardAction(this, "search", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED); // ���� ������ �� "search" �̺�Ʈ �߻�
		jp.add(jt);
		js1.setLocation(50, 145);
		js1.setSize(395, 30);
		js1.setBackground(Color.white);
		js1.setOpaque(false);
		js1.addMouseListener(new Mp3Stick());
		js2.setLocation(400, 34);
		js2.setSize(30, 90);
		js2.setBackground(Color.white);
		js2.setOpaque(false);
		js2.setToolTipText("���� ũ�⸦ �����մϴ�.");
		js2.addMouseListener(new VolumeStick());
		js2.addChangeListener(new ChangeListener() { // ���� ���� �����̴��� ��꿡 ��ȭ�� �Ͼ�� �� �߻��Ǵ� �̺�Ʈ ó��
			@Override
			public void stateChanged(ChangeEvent e) {
				if (player != null) {
					if ((double) js2.getValue() / 100 >= 0.5) {
						player.setVolume((((double) js2.getValue() / 100) * 7.0 / 5.0) - 0.4);
					} else
						player.setVolume(((double) js2.getValue() / 100 * 2 * (double) js2.getValue() / 100 * 2) * 0.3);
				}
				vol.setText("Vol : " + js2.getValue());
			}
		});
		jp.add(presentimes);
		song.setText("");
		song.setFont(new Font("���õ������ Medium", Font.PLAIN, 20));
		song.setForeground(Color.ORANGE);
		song.setSize(500,20);
		song.setLocation(0, 123);
		song.setHorizontalAlignment(JLabel.CENTER);
		jp.add(song);
		vol.setVisible(false);
		vol.setFont(new Font("���õ������ Medium", Font.PLAIN, 23));
		vol.setForeground(Color.RED);
		vol.setSize(100, 25);
		vol.setLocation(375, 12);
		vol.setText("Vol : " + js2.getValue());
		jp.add(vol);
		presentimes.setSize(55, 20);
		presentimes.setLocation(45, 180);
		presentimes.setFont(new Font("HY����M", Font.BOLD, 15));
		presentimes.setForeground(Color.BLUE);
		jp.add(lastimes);
		lastimes.setSize(55, 20);
		lastimes.setLocation(386, 180);
		lastimes.setFont(new Font("HY����M", Font.BOLD, 15));
		lastimes.setForeground(Color.BLUE);
		chooser.setPreferredSize(new Dimension(700, 500));
		setFileChooserFont(chooser.getComponents());
		chooser.setFileFilter(filter);
		chooser.setMultiSelectionEnabled(true);
		chooser.setDragEnabled(true);
		savelists.setLocation(52, 620);
		savelists.setSize(117, 30);
		savelists.addActionListener(this);
		savelists.setToolTipText("PlayList�� ���Ϸ� �����մϴ�.");
		jp.add(savelists);
		openlists.setLocation(174, 620);
		openlists.setSize(117, 30);
		openlists.addActionListener(this);
		openlists.setToolTipText("����� PlayList�� �ҷ��ɴϴ�.");
		jp.add(openlists);
		jp.setLayout(null); // setLayout�� null�� �ؾ��� ���� ������ ��� ��
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // â ������ ���μ����� ���� ����
	}
	// JFileChooser ���̾�α� â ����
	public void setFileChooserFont(Component[] comp) {
		for(int i = 0; i < comp.length; i++) {
			if(comp[i] instanceof Container)
				setFileChooserFont(((Container)comp[i]).getComponents());
			try {
				comp[i].setFont(new Font("���õ������ Light", Font.BOLD, 15));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// �� ��ư���� �߰��� �׼� �̺�Ʈ ó��(�˻� ��ư, �߰� ��ư, ���� ��ư, ��� �������� ��ư, ��� �ҷ����� ��ư, ���/�Ͻ����� ��ư, ������ ��ư, ������ ��ư)
	@SuppressWarnings("unchecked")
	@Override // ActionListener�� �����ϱ� ���ؼ� �߻� �޼ҵ� actionPerformed�� �������̵��ϸ� ��!
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("search")) { // �˻� ��ư �����ų� �˻��� �Է��ϰ� ���� �Է����� ��
			String title = jt.getText();
			if(title.equals("")) { // �ƹ��͵� �Է����� �ʰ� �˻��ϸ�
				JOptionPane.showMessageDialog(null, "�˻�� �Է��� �ּ���!", "ã�� ����", JOptionPane.WARNING_MESSAGE);
				return;
			}
			ArrayList<Integer> results = new ArrayList<Integer>();
			for(int i = 0; i < listmodel.getSize(); i++)
				if(listmodel.getElementAt(i).contains(title)) // ����Ʈ�� i��° index�� �˻�� �����ϰ� ���� ���
					results.add(i);
			int[] result = new int[results.size()];
			for(int i = 0; i < result.length; i++)
				result[i] = results.get(i);
			if(result.length == 0) { // result �迭�� �ƹ��͵� ������ �� �Ǿ��� ��
				JOptionPane.showMessageDialog(null, "ã�� �뷡�� �����ϴ�!", "ã�� ����", JOptionPane.WARNING_MESSAGE);
				return;
			}
			jl.setSelectedIndices(result);
		} else { // �װ� �ƴ϶�� 
			JButton pressedbutton = (JButton) e.getSource(); // �� actionPerformed �޼ҵ尡 ����Ǿ��� �� Ŭ���� ��ư�� �ҷ���
			if (pressedbutton.equals(addb)) { // �߰� ��ư Ŭ������ ��
				int option = chooser.showOpenDialog(new JFrame());
				if(option == JFileChooser.APPROVE_OPTION) {
					File[] selectedfiles = chooser.getSelectedFiles(); // ���� ���� �� �����ϰ� Ȯ�� ������ �� ���ϵ��� selectedfiles �迭�� �����
					for(int i = 0; i < selectedfiles.length; i++) {
						try {
							MP3File mp3 = (MP3File)AudioFileIO.read(selectedfiles[i]);
							Tag tag = mp3.getTag();
							listmodel.addElement(tag.getFirst(FieldKey.TITLE)); // �ش� mp3 ������ ������ �����ؼ� list�� �߰�
							filelists.add(selectedfiles[i]); // filelists�� ���� �߰�
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, "mp3 Ȯ���ڸ� �����մϴ�.", "����", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} else if(pressedbutton.equals(deleteb)) { // ���� ��ư ������ ��
				int[] selected = jl.getSelectedIndices();
				for(int i = 0; i < selected.length; i++) {
					selected[i] -= i; // �ϳ� ������ ������ index�� 1�� ������Ƿ�
					File deletingfile = filelists.get(selected[i]);
					if(m != null) // �뷡�� ��� ���̾��� ��
						if(m.getSource().toString().equals(deletingfile.toURI().toString())) { // �����ҷ��� �뷡�� ��� �� �϶�
							t.stop();
							player.stop();
							player = null;
							m = null;
							deleteb.setEnabled(false);
							jb[0].setIcon(buttonimgs[0]); // ��� ��ư���� �ٲ�
							song.setText("");
							js1.setVisible(false);
							presentimes.setText("");
							lastimes.setText("");
						} else { // �׷��� ���� �� ���� ��� ���̴� �뷡 �״�� ���õ� ��� ǥ��
							if(m.getSource().toString().equals(filelists.get(i).toURI().toString()))
								jl.setSelectedIndex(i);
						}
					else
						deleteb.setEnabled(false);
					listmodel.remove(selected[i]);
					filelists.remove(deletingfile);
				}
			} else if(pressedbutton.equals(savelists)) { // ��� �������� ��ư Ŭ������ ��
				ObjectOutputStream oos = null;
				int choice = chooser.showSaveDialog(null);
				if(choice == JFileChooser.APPROVE_OPTION) {
					try {
						File path = chooser.getCurrentDirectory().getAbsoluteFile(); // JFileChooser���� ����ڰ� ���� ���� ��θ� path�� ����
						oos = new ObjectOutputStream(new FileOutputStream(path + "\\PlayList.dat")); // �� ��ο� PlayList.dat���� ����
						oos.writeObject(filelists); // �� PlayList.dat���Ͽ� ArrayList<File> Ÿ���� ��ü filelists ����
						oos.flush();
					}catch(Exception e2) {
						e2.printStackTrace();
					}
				}
			} else if(pressedbutton.equals(openlists)) { // ��� �ҷ����� ��ư Ŭ������ ��
				ObjectInputStream ois = null;
				int choice = chooser.showOpenDialog(null);
				if(choice == JFileChooser.APPROVE_OPTION) {
					try {
						if(m != null)
							player.stop();
						song.setText("");
						js1.setVisible(false);
						presentimes.setText("");
						lastimes.setText("");
						t.stop();
						listmodel.clear(); // ���� PlayList �ʱ�ȭ
						player = null;
						m = null;
						jb[0].setIcon(buttonimgs[0]); // ��� ��ư���� �ٲ�
						deleteb.setEnabled(false);
						ois = new ObjectInputStream(new FileInputStream(chooser.getSelectedFile())); 
						filelists = (ArrayList<File>)ois.readObject(); // ����ڰ� ������ ���Ͽ� ����� ��ü ������ �о filelists�� ����
						for(int i = 0; i < filelists.size(); i++) {
							MP3File mp3 = (MP3File)AudioFileIO.read(filelists.get(i));
							Tag tag = mp3.getTag();
							listmodel.addElement(tag.getFirst(FieldKey.TITLE));
						}
					} catch (Exception e3) {
						JOptionPane.showMessageDialog(null, "��ȿ�� ������ �ƴմϴ�!", "���� �ҷ����� ����", JOptionPane.ERROR_MESSAGE);
					}
				}
			} else if (pressedbutton.getIcon().equals(buttonimgs[0])) { // ��� ��ư Ŭ������ ��
				deleteb.setEnabled(true);
				if(jl.getSelectedIndices().length > 0) { // ��� ��Ͽ� �׸��� �ϳ� �̻� �������� ���
					int num = jl.getSelectedIndices()[0]; 
					jl.setSelectedIndex(num); // �� �׸� ���õ� �ɷ� ǥ��
					if(m != null) { // �� �� �̻� ��� ��ư�� ���� ���� �ִٸ�
						if(!m.getSource().toString().equals(filelists.get(num).toURI().toString())) {
							m = new Media(filelists.get(num).toURI().toString());
							player = new MediaPlayer(m);
						} // �÷��� ���̾��� �뷡�� ������ �뷡�� �ٸ� �� ������ �뷡 ���
					} else { // ��� ��ư ���� �� ���ٸ� (MP3 �÷��̾ �����ϰ� ���ϴ� �뷡 ������ ���� ��� ��ư ���� ���) ������ �뷡 ���
						m = new Media(filelists.get(num).toURI().toString());
						player = new MediaPlayer(m);
					} 
				} else { // ��� ��Ͽ� �׸��� �������� �ʾ��� �Ͽ� (MP3 �÷��̾� �����ϰ� �ٷ� ��� ��ư �����ų� ��� ���̾��� �뷡 ���� �� ��� ��ư ����)
					if(listmodel.getSize() > 0) { // ��� ��Ͽ� �뷡�� �ϳ� �̻� �ִٸ�
						jl.setSelectedIndex(0); 
						m = new Media(filelists.get(0).toURI().toString());
						player = new MediaPlayer(m);
					} else { // ��� ��Ͽ� �뷡�� ���ٸ�
						deleteb.setEnabled(false);
						JOptionPane.showMessageDialog(null, "�뷡�� �߰��ϼ���!", "��� ����", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				t.start();
				js1.setVisible(true);
				player.play();
				song.setText(jl.getSelectedValue());
				pressedbutton.setToolTipText("�뷡�� ��� ����ϴ�.");
				pressedbutton.setIcon(buttonimgs[3]); // ��ư�� �̹����� "�Ͻ�����.png"�� �ٲ�
			} else if (pressedbutton.getIcon().equals(buttonimgs[3])) { // �Ͻ� ���� ��ư Ŭ������ ��
				player.pause(); // �뷡 �Ͻ� ����
				pressedbutton.setToolTipText("PlayList���� ������ �뷡�� ����մϴ�.");
				pressedbutton.setIcon(buttonimgs[0]); // ��ư �̹����� "���.png"�� �ٲ�
			} else if (pressedbutton.getIcon().equals(buttonimgs[1]) || 
					pressedbutton.getIcon().equals(buttonimgs[2])) { // ������ �Ǵ� ������ ��ư Ŭ������ ��
				if (m != null) {
					int n = jl.getSelectedIndex();
					if(pressedbutton.getIcon().equals(buttonimgs[1])) { // ������ Ŭ�� ��
						if (n > 0) {
							player.stop();
							jl.setSelectedIndex(n - 1);
							m = new Media(filelists.get(n - 1).toURI().toString());
							player = new MediaPlayer(m);
							player.play();
							t.start();
						}
					} else { // ������ Ŭ�� ��
						if (n < jl.getLastVisibleIndex()) {
							player.stop();
							jl.setSelectedIndex(n + 1);
							m = new Media(filelists.get(n + 1).toURI().toString());
							player = new MediaPlayer(m);
							player.play();
							t.start();
						}
					}
					song.setText(jl.getSelectedValue());
				}
			} else { // ��ü �� �� ��� ��ư, ��ü �ݺ� ��� ��ư �Ǵ� ���� ��� ��ư Ŭ�� ��
				pressedbutton.setEnabled(false); // Ŭ���� ��ư ��Ȱ��ȭ
				if(pressedbutton.getIcon().equals(optionimgs[0])) { // ��ü �� �� ��� ��ư Ŭ������ ���
					options[1].setEnabled(true);
					options[2].setEnabled(true);
				} else if(pressedbutton.getIcon().equals(optionimgs[1])) { // ��ü �ݺ� ��� ��ư Ŭ������ ���
					options[0].setEnabled(true);
					options[2].setEnabled(true);
				} else { // ���� ��� Ŭ������ ���
					options[0].setEnabled(true);
					options[1].setEnabled(true);
				}
			}
		}
	}
}