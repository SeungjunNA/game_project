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

	JFrame frame = new JFrame(); // 전체 담을 프레임 생성

	Panel panel; // 게임 진행 패널
	JPanel buttonPanel; // 버튼 패널
	JPanel scorePanel; // 점수 패널

	JButton playButton; // 시작버튼
	JButton stopButton; // 정지버튼
	JButton continueButton; // 계속하기버튼
	JButton replayButton;
	JButton endButton; // 종료버튼

	JLabel scoreLabel; // 점수 라벨
	int score = 0; // 점수

	Airplane plane = new Airplane(300, 700, 100, 100);; // 비행기 생성

	Timer t; // 그래픽움직이기위한 타이머

	int time1 = 0; // 시간당 점수를 올리기위함
	int ufoTime = 0; // 시간당 ufo를 만들기 위함
	int missileTime = 0; // 시간당 비행기에서 발사할 미사일 만들기 위함
	int beamTime = 0; // 시간당 ufo에서 발사할 beam 만들기 위함
	int heart = 3; // 목숨

	LifeIcon life1 = new LifeIcon("life.png", 10, 10, 40, 40); // 목숨 이미지
	LifeIcon life2 = new LifeIcon("life.png", 60, 10, 40, 40);
	LifeIcon life3 = new LifeIcon("life.png", 110, 10, 40, 40);

	Beam bm; // 충돌시 beamList에서 사라지게 하기 위함
	Missile ms; // 충돌시 missileList에서 사라지게 하기 위함
	Ufo uf; // 충돌시 ufoList에서 사라지게 하기 위함
	LifeIcon lf; // plane이 하트를 먹을경우 lifeList2에서 사라지게 하기 위함

	boolean background = false; // 게임 화면 전환
	boolean end = false; // 게임종료 됬는지

	ArrayList<Ufo> ufoList = new ArrayList<>(); // ufo
	ArrayList<Missile> missileList = new ArrayList<>(); // 미사일
	ArrayList<Beam> beamList = new ArrayList<>(); // beam
	ArrayList<LifeIcon> lifeList1 = new ArrayList<>(); // 목숨
	ArrayList<LifeIcon> lifeList2 = new ArrayList<>();

	Audio backgroundSound = new Audio("배경음악.wav", true); // 게임 배경음악
	Audio planeHitSound = new Audio("효과음1.wav", false); // 비행기가 ufo 또는 beam 충돌시 효과음
	Audio ufoHitSound = new Audio("효과음2.wav", false); // ufo가 missile 충돌시 효과음
	Audio endSound = new Audio("효과음3.wav", false); // life가 다 없어지고 게임종료시 효과음
	Audio lifeSound = new Audio("띠링.wav", false);

	static String playerName;

	GamePlay() {
		panel = new Panel();
		buttonPanel = new JPanel();
		scorePanel = new JPanel();
		playButton = new JButton("시작하기");
		stopButton = new JButton("일시정지");
		continueButton = new JButton("계속하기");
		replayButton = new JButton("다시하기");
		endButton = new JButton("종료");
		scoreLabel = new JLabel(playerName + "           점수 : " + score);

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
		// 처음에 key반응 안하도록
		panel.requestFocus();
		panel.setFocusable(false);
	}

	public static void main(String[] args) {
		playerName = JOptionPane.showInputDialog("이름을 입력해주세요 :");
		new GamePlay();

	}

	// 게임 패널
	class Panel extends JPanel {
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			ImageIcon img = new ImageIcon("배경.jpg");
			ImageIcon img1 = new ImageIcon("비행기.png");
			if (background == true) {// background가 true로 변경시 게임화면으로전환
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
				if (end) { // 게임화면에서 life다없어져 end가 true변경시 게임종료 점수 표시
					g2d.setColor(Color.black);
					g2d.fillRect(0, 0, panel.WIDTH, panel.HEIGHT);
					g2d.setColor(Color.red);
					g2d.setFont(new Font("휴먼매지직체", Font.BOLD, 100));
					g2d.drawString("GAME", 200, 250);
					g2d.drawString("OVER", 210, 350);
					g2d.setColor(Color.black);
					g2d.setFont(new Font("휴먼매지직체", Font.BOLD, 50));
					g2d.drawString("score", 290, 450);
					g2d.drawString("" + score, 310, 500);
					buttonActive(REPLAY + END);
				}
				// 게임 시작화면으로 전환되면 key반응 하도록
				panel.requestFocus();
				panel.setFocusable(true);
			} else { // 처음 background가 false로 시작화면
				g2d.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
				g2d.setColor(Color.black);
				g2d.setFont(new Font("휴먼매지직체", Font.BOLD, 100));
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
			// 방향키로 움직이고 스페이스바 누를경우 missile 생성
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
			scoreLabel.setText(playerName + "            점수 : " + score);
			// 시간당 ufo 생성후 move로 이동
			if (ufoTime++ == 100) {
				ufoList.add(new Ufo((int) (Math.random() * (panel.getWidth() - 70)), 0, 100, 100));
				ufoTime = 0;
			}
			for (Ufo pi : ufoList) {
				pi.move();
			}
			// 시간당 plane위치에서 미사일 발사
			if (missileTime++ == 20) {
				missileList.add(new Missile(plane.pX + 25, plane.pY, 50, 50));
				missileTime = 0;
			}
			for (Missile pi : missileList) {
				pi.move();
			}
			// 시간당 ufo위치에서 beam발사
			if (beamTime++ == 100) {
				for (Ufo pi : ufoList)
					beamList.add(new Beam(pi.pX + 40, pi.pY + 50, 30, 30));
				beamTime = 0;
			}
			for (Beam pi : beamList) {
				pi.move();
			}
			// 랜덤으로 0이나오면 하트떨어지게 만듦
			if ((int) (Math.random() * 1000) == 0) {
				lifeList2.add(new LifeIcon("life.png", (int) (Math.random() * (panel.getWidth() - 70)), 0, 50, 50));
			}
			for (LifeIcon pi : lifeList2) {
				pi.move();
			}
			// plane이 하트를 먹을경우 목숨의 개수에 따라 점수 또는 목숨이 올라감, 떨어지는 하트가 panel밖으로 나갈경우 삭제
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
			// beam이 plane과 충돌시 목숨 깎이도록, 내려오는 beam이 panel밖으로 나갈경우 삭제
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
						if (heart == 0) { // 목숨이 0이되면 게임 종료
							t.stop();
							backgroundSound.stop();
							endSound.play();
							end = true;
							panel.setFocusable(false);
						}
					}
				}
			}
			// missile이 ufo와 충돌시 충돌한 둘다 제거하고 점수 올림, 발사한 미사일이 panel밖으로 나갈경우 삭제
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
			// ufo가 plane과 충돌시 목숨 전부 없어지고 게임종료, 내려오는 ufo가 panel밖으로 나갈경우 삭제
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

			// 시간당 점수 올라감
			if (time1++ == 100) {
				score += 10;
				time1 = 0;
			}
			frame.repaint();
		}
	}

	// plane 만드는 클래스
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
			ImageIcon img = new ImageIcon("비행기.png");
			g2d.drawImage(img.getImage(), pX, pY, width, height, null);
		}

		public boolean hit(Point p) {
			return (pX <= p.x && pX + width >= p.x) && (pY < p.y && pY + height >= p.y);
		}
	}

	// missile만드는 클래스
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

	// ufo만드는 클래스
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

	// beam 만드는 클래스
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

	// life 만드는 파일
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
