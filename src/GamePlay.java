import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePlay extends JFrame {
	private final int PLAY = 1;
	private final int STOP = 2;
	private final int CONTINUE = 4;
	private final int REPLAY = 8;
	private final int END = 16;

	JFrame frame = new JFrame(); // ��ü ���� ������ ����

	Panel panel; // ���� ���� �г�
	JPanel buttonPanel; // ��ư �г�
	JPanel scorePanel; // ���� �г�

	JButton playButton; // ���۹�ư
	JButton stopButton; // ������ư
	JButton continueButton; // ����ϱ��ư
	JButton replayButton;
	JButton endButton; // �����ư

	JLabel scoreLabel; // ���� ��
	int score = 0; // ����

	Airplane plane = new Airplane(300, 700, 100, 100);; // ����� ����

	Timer t; // �׷��ȿ����̱����� Ÿ�̸�

	int time1 = 0; // �ð��� ������ �ø�������
	int ufoTime = 0; // �ð��� ufo�� ����� ����
	int missileTime = 0; // �ð��� ����⿡�� �߻��� �̻��� ����� ����
	int beamTime = 0; // �ð��� ufo���� �߻��� beam ����� ����
	int heart = 3; // ���

	LifeIcon life1 = new LifeIcon("life.png", 10, 10, 40, 40); // ��� �̹���
	LifeIcon life2 = new LifeIcon("life.png", 60, 10, 40, 40);
	LifeIcon life3 = new LifeIcon("life.png", 110, 10, 40, 40);

	Beam bm; // �浹�� beamList���� ������� �ϱ� ����
	Missile ms; // �浹�� missileList���� ������� �ϱ� ����
	Ufo uf; // �浹�� ufoList���� ������� �ϱ� ����
	LifeIcon lf; // plane�� ��Ʈ�� ������� lifeList2���� ������� �ϱ� ����

	boolean background = false; // ���� ȭ�� ��ȯ
	boolean end = false; // �������� �����

	ArrayList<Ufo> ufoList = new ArrayList<>(); // ufo
	ArrayList<Missile> missileList = new ArrayList<>(); // �̻���
	ArrayList<Beam> beamList = new ArrayList<>(); // beam
	ArrayList<LifeIcon> lifeList1 = new ArrayList<>(); // ���
	ArrayList<LifeIcon> lifeList2 = new ArrayList<>();

	Audio backgroundSound = new Audio("�������.wav", true); // ���� �������
	Audio planeHitSound = new Audio("ȿ����1.wav", false); // ����Ⱑ ufo �Ǵ� beam �浹�� ȿ����
	Audio ufoHitSound = new Audio("ȿ����2.wav", false); // ufo�� missile �浹�� ȿ����
	Audio endSound = new Audio("ȿ����3.wav", false); // life�� �� �������� ��������� ȿ����
	Audio lifeSound = new Audio("�층.wav", false);

	static String playerName;

	GamePlay() {
		panel = new Panel();
		buttonPanel = new JPanel();
		scorePanel = new JPanel();
		playButton = new JButton("�����ϱ�");
		stopButton = new JButton("�Ͻ�����");
		continueButton = new JButton("����ϱ�");
		replayButton = new JButton("�ٽ��ϱ�");
		endButton = new JButton("����");
		scoreLabel = new JLabel(playerName + "           ���� : " + score);

		playButton.addActionListener(new ButtonListener());
		stopButton.addActionListener(new ButtonListener());
		continueButton.addActionListener(new ButtonListener());
		replayButton.addActionListener(new ButtonListener());
		endButton.addActionListener(new ButtonListener());
		buttonPanel.add(playButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(continueButton);
		buttonPanel.add(replayButton);
		buttonPanel.add(endButton);

		scorePanel.add(scoreLabel);

		lifeList1.add(life1);
		lifeList1.add(life2);
		lifeList1.add(life3);

		frame.add(BorderLayout.NORTH, scorePanel);
		frame.add(BorderLayout.SOUTH, buttonPanel);
		frame.add(BorderLayout.CENTER, panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 900);
		frame.setVisible(true);

		t = new Timer(10, new TimerListener());
		panel.addKeyListener(new DirectionListener());
		// ó���� key���� ���ϵ���
		panel.requestFocus();
		panel.setFocusable(false);
	}

	public static void main(String[] args) {
		playerName = JOptionPane.showInputDialog("�̸��� �Է����ּ��� :");
		new GamePlay();

	}

	// ���� �г�
	class Panel extends JPanel {
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			ImageIcon img = new ImageIcon("���.jpg");
			ImageIcon img1 = new ImageIcon("�����.png");
			if (background == true) {// background�� true�� ����� ����ȭ��������ȯ
				g2d.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
				plane.draw(g2d);
				for (Missile pi : missileList)
					pi.draw(g2d);
				for (Ufo pi : ufoList)
					pi.draw(g2d);
				for (Beam pi : beamList)
					pi.draw(g2d);
				for (LifeIcon pi : lifeList1)
					pi.draw(g2d);
				for (LifeIcon pi : lifeList2)
					pi.draw(g2d);
				if (end) { // ����ȭ�鿡�� life�پ����� end�� true����� �������� ���� ǥ��
					g2d.setColor(Color.black);
					g2d.fillRect(0, 0, panel.WIDTH, panel.HEIGHT);
					g2d.setColor(Color.red);
					g2d.setFont(new Font("�޸ո�����ü", Font.BOLD, 100));
					g2d.drawString("GAME", 200, 250);
					g2d.drawString("OVER", 210, 350);
					g2d.setColor(Color.black);
					g2d.setFont(new Font("�޸ո�����ü", Font.BOLD, 50));
					g2d.drawString("score", 290, 450);
					g2d.drawString("" + score, 310, 500);
					buttonActive(REPLAY + END);
				}
				// ���� ����ȭ������ ��ȯ�Ǹ� key���� �ϵ���
				panel.requestFocus();
				panel.setFocusable(true);
			} else { // ó�� background�� false�� ����ȭ��
				g2d.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
				g2d.setColor(Color.black);
				g2d.setFont(new Font("�޸ո�����ü", Font.BOLD, 100));
				g2d.drawString("shooting", 150, 250);
				g2d.drawString("game", 240, 350);
				buttonActive(PLAY);
			}
		}
	}

	class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == playButton) {
				t.start();
				background = true;
				panel.setFocusable(true);
				backgroundSound.play();
				buttonActive(STOP + CONTINUE + END);
			} else if (e.getSource() == stopButton) {
				t.stop();
				backgroundSound.stop();
				panel.setFocusable(false);
				buttonActive(CONTINUE + REPLAY + END);
			} else if (e.getSource() == continueButton) {
				t.start();
				backgroundSound.play();
				panel.setFocusable(true);
				buttonActive(STOP + END);
			} else if (e.getSource() == replayButton) {
				t.start();
				backgroundSound.play();
				ufoList.removeAll(ufoList);
				beamList.removeAll(beamList);
				missileList.removeAll(missileList);
				end = false;
				score = 0;
				lifeList1.add(life1);
				lifeList1.add(life2);
				lifeList1.add(life3);
				heart = 3;
				plane.pX = 300;
				plane.pY = 700;
				buttonActive(STOP + END);
			} else if (e.getSource() == endButton)
				System.exit(0);
		}
	}

	private void buttonActive(int flag) {
		if ((flag & PLAY) != 0)
			playButton.setEnabled(true);
		else
			playButton.setEnabled(false);
		if ((flag & STOP) != 0)
			stopButton.setEnabled(true);
		else
			stopButton.setEnabled(false);
		if ((flag & CONTINUE) != 0)
			continueButton.setEnabled(true);
		else
			continueButton.setEnabled(false);
		if ((flag & REPLAY) != 0)
			replayButton.setEnabled(true);
		else
			replayButton.setEnabled(false);
		if ((flag & END) != 0)
			endButton.setEnabled(true);
		else
			endButton.setEnabled(false);
	}

	class DirectionListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();
			// ����Ű�� �����̰� �����̽��� ������� missile ����
			switch (keycode) {
			case KeyEvent.VK_UP:
				plane.move(0, -10);
				break;
			case KeyEvent.VK_DOWN:
				plane.move(0, 10);
				break;
			case KeyEvent.VK_RIGHT:
				plane.move(10, 0);
				break;
			case KeyEvent.VK_LEFT:
				plane.move(-10, 0);
				break;
			/*
			 * case KeyEvent.VK_SPACE: missileList.add(new Missile(plane.pX + 25, plane.pY,
			 * 50, 50)); break;
			 */
			}
			frame.repaint();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}
	}

	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			scoreLabel.setText(playerName + "            ���� : " + score);
			// �ð��� ufo ������ move�� �̵�
			if (ufoTime++ == 100) {
				ufoList.add(new Ufo((int) (Math.random() * (panel.getWidth() - 70)), 0, 100, 100));
				ufoTime = 0;
			}
			for (Ufo pi : ufoList) {
				pi.move();
			}
			// �ð��� plane��ġ���� �̻��� �߻�
			if (missileTime++ == 20) {
				missileList.add(new Missile(plane.pX + 25, plane.pY, 50, 50));
				missileTime = 0;
			}
			for (Missile pi : missileList) {
				pi.move();
			}
			// �ð��� ufo��ġ���� beam�߻�
			if (beamTime++ == 100) {
				for (Ufo pi : ufoList)
					beamList.add(new Beam(pi.pX + 40, pi.pY + 50, 30, 30));
				beamTime = 0;
			}
			for (Beam pi : beamList) {
				pi.move();
			}
			// �������� 0�̳����� ��Ʈ�������� ����
			if ((int) (Math.random() * 1000) == 0) {
				lifeList2.add(new LifeIcon("life.png", (int) (Math.random() * (panel.getWidth() - 70)), 0, 50, 50));
			}
			for (LifeIcon pi : lifeList2) {
				pi.move();
			}
			// plane�� ��Ʈ�� ������� ����� ������ ���� ���� �Ǵ� ����� �ö�, �������� ��Ʈ�� panel������ ������� ����
			for (int i = 0; i < lifeList2.size(); i++) {
				lf = lifeList2.get(i);
				if (lf.pY > panel.getHeight())
					lifeList2.remove(i);
				if (plane.hit(new Point(lf.pX, lf.pY))) {
					lifeSound.play();
					lifeList2.remove(i);
					if (heart == 1) {
						lifeList1.add(life2);
						heart++;
					} else if (heart == 2) {
						lifeList1.add(life3);
						heart++;
					} else if (heart == 3)
						score += 100;
				}
			}
			// beam�� plane�� �浹�� ��� ���̵���, �������� beam�� panel������ ������� ����
			for (int i = 0; i < beamList.size(); i++) {
				bm = beamList.get(i);
				if (bm.pY > panel.getHeight())
					beamList.remove(i);
				if (plane.hit(new Point(bm.pX, bm.pY))) {
					if (heart > 0) {
						planeHitSound.play();
						beamList.remove(i);
						heart--;
						lifeList1.remove(heart);
						if (heart == 0) { // ����� 0�̵Ǹ� ���� ����
							t.stop();
							backgroundSound.stop();
							endSound.play();
							end = true;
							panel.setFocusable(false);
						}
					}
				}
			}
			// missile�� ufo�� �浹�� �浹�� �Ѵ� �����ϰ� ���� �ø�, �߻��� �̻����� panel������ ������� ����
			for (int i = 0; i < missileList.size(); i++) {
				ms = missileList.get(i);
				if (ms.pY < 0)
					missileList.remove(i);
				for (int j = 0; j < ufoList.size(); j++) {
					uf = ufoList.get(j);
					if (uf.hit(new Point(ms.pX, ms.pY))) {
						ufoHitSound.play();
						missileList.remove(i);
						ufoList.remove(j);
						score += 50;
					}
				}
			}
			// ufo�� plane�� �浹�� ��� ���� �������� ��������, �������� ufo�� panel������ ������� ����
			for (int i = 0; i < ufoList.size(); i++) {
				uf = ufoList.get(i);
				if (plane.hit(new Point(uf.pX, uf.pY))) {
					planeHitSound.play();
					ufoList.remove(i);
					lifeList1.removeAll(lifeList1);
					t.stop();
					backgroundSound.stop();
					endSound.play();
					end = true;
					panel.setFocusable(false);
				}
				if (uf.pY > panel.getHeight())
					ufoList.remove(i);
			}

			// �ð��� ���� �ö�
			if (time1++ == 100) {
				score += 10;
				time1 = 0;
			}
			frame.repaint();
		}
	}

	// plane ����� Ŭ����
	class Airplane {
		int pX, pY;
		int width, height;

		public Airplane(int x, int y, int width, int height) {
			pX = x;
			pY = y;
			this.width = width;
			this.height = height;
		}

		public void move(int x, int y) {
			pX += x;
			pY += y;
		}

		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			ImageIcon img = new ImageIcon("�����.png");
			g2d.drawImage(img.getImage(), pX, pY, width, height, null);
		}

		public boolean hit(Point p) {
			return (pX <= p.x && pX + width >= p.x) && (pY < p.y && pY + height >= p.y);
		}
	}

	// missile����� Ŭ����
	class Missile {
		int pX, pY;
		int width, height;

		Missile(int x, int y, int width, int height) {
			pX = x;
			pY = y;
			this.width = width;
			this.height = height;
		}

		public void move() {
			pY -= 10;
		}

		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			ImageIcon img = new ImageIcon("missile.png");
			g2d.drawImage(img.getImage(), pX, pY, width, height, null);
		}
	}

	// ufo����� Ŭ����
	class Ufo {
		int pX, pY;
		int width, height;

		Ufo(int x, int y, int width, int height) {
			pX = x;
			pY = y;
			this.width = width;
			this.height = height;
		}

		public void move() {
			pY += 1;
		}

		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			ImageIcon img = new ImageIcon("ufo1.png");
			g2d.drawImage(img.getImage(), pX, pY, width, height, null);
		}

		public boolean hit(Point p) {
			return (pX <= p.x && pX + width >= p.x) && (pY < p.y && pY + height >= p.y);
		}

	}

	// beam ����� Ŭ����
	class Beam {
		int pX, pY;
		int width, height;

		Beam(int x, int y, int width, int height) {
			pX = x;
			pY = y;
			this.width = width;
			this.height = height;
		}

		public void move() {
			pY += 8;
		}

		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			ImageIcon img = new ImageIcon("beam.png");
			g2d.drawImage(img.getImage(), pX, pY, width, height, null);
		}

	}

	// life ����� ����
	class LifeIcon extends ImageIcon {
		int pX, pY;
		int width, height;

		LifeIcon(String img, int x, int y, int width, int height) {
			super(img);
			pX = x;
			pY = y;
			this.width = width;
			this.height = height;
		}

		public void move() {
			pY += 5;
		}

		public void draw(Graphics g) {
			g.drawImage(this.getImage(), pX, pY, width, height, null);
		}
	}

}
