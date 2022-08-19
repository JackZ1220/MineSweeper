import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper extends JFrame implements ActionListener, MouseListener{
	JPanel boardPanel;
	JToggleButton[][] board;
	JButton b;
	JMenu menu;
	JMenuItem beginner, intermediate, hard;
	JMenuBar menuBar;
	JPanel panel;
	JLabel timerLabel;

	int time = 0;
	Timer timer;
	int dimR = 9, dimC = 9;

	int difficulty = 1;
	int numSquaresLeft;

	ImageIcon flag;
	ImageIcon [] numIcons;
	ImageIcon mines;
	ImageIcon smile, dead, win;

	boolean firstClick = true, gameOn = true;
	int numMines = 10;

	public Minesweeper(){

		flag = new ImageIcon("flag.png");
		flag = new ImageIcon(flag.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));

		mines = new ImageIcon("mine.png");
		mines = new ImageIcon(mines.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));

		smile = new ImageIcon("smile0.png");
		smile = new ImageIcon(smile.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));

		dead = new ImageIcon("dead0.png");
		dead = new ImageIcon(dead.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));

		win = new ImageIcon("win0.png");
		win = new ImageIcon(win.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));

		numIcons = new ImageIcon[8];


		for(int i = 1; i < numIcons.length-1; i++){
			numIcons[i] = new ImageIcon((i)+".png");
			numIcons[i] = new ImageIcon(numIcons[i].getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		}


		panel = new JPanel(new GridLayout(1, 3, 5, 5));

		menuBar = new JMenuBar();
		menu = new JMenu();

		panel.add(menuBar);

		this.add(panel, BorderLayout.NORTH);

		menuBar.add(menu);

		menu.setText("Difficulty");

		beginner = new JMenuItem();
		beginner.setText("Beginner");
		menu.add(beginner);
		beginner.addActionListener(this);

		intermediate = new JMenuItem();
		intermediate.setText("Intermediate");
		menu.add(intermediate);
		intermediate.addActionListener(this);

		hard = new JMenuItem();
		hard.setText("Hard");
		menu.add(hard);
		hard.addActionListener(this);

		b = new JButton(smile);
		panel.add(b);
		b.addActionListener(this);

		timerLabel = new JLabel();
		timerLabel.setText("Timer: 0");
		panel.add(timerLabel);

		createBoard(dimR, dimC);
		this.setVisible(true);
	}

	public void createBoard(int row, int col){

		if(boardPanel != null)
			this.remove(boardPanel);

		firstClick = true;
		gameOn = true;

		board = new JToggleButton[row][col];
		boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(row, col));

		for(int r = 0; r < board.length; r++){
			for(int c = 0; c < board[r].length; c++){
				board[r][c] = new JToggleButton();
				board[r][c].putClientProperty("row", r);
				board[r][c].putClientProperty("col", c);
				board[r][c].putClientProperty("state", 0);
				board[r][c].addMouseListener(this);
				boardPanel.add(board[r][c]);
			}
		}

		this.add(boardPanel);
		this.setSize(board[0].length * 35, board.length * 35);



		numSquaresLeft = board.length * board[0].length;

		this.revalidate();
	}

	public void setBombsAndNums(int selectedRow, int selectedCol){
		int count = numMines;
		while(count > 0){
			int row = (int)(Math.random()*dimR);
			int col = (int)(Math.random()*dimC);

			int state = Integer.parseInt(""+board[row][col].getClientProperty("state"));

			if(state == 0 && (row < selectedRow - 1 || col < selectedCol - 1 || row > selectedRow + 1 || col > selectedCol + 1)){
				board[row][col].putClientProperty("state", -1);
				count--;
			}
		}

		for(int r = 0; r < dimR; r++){
			for(int c = 0; c < dimC; c++){
				count = 0;

				int state = Integer.parseInt("" + board[r][c].getClientProperty("state"));

				if(state != -1){

					for(int smallR = r-1; smallR <= r+1; smallR++){
						for(int smallC = c - 1; smallC <= c+1;  smallC++){
							try{
								state = Integer.parseInt("" + board[smallR][smallC].getClientProperty("state"));
								if(state == -1 && (smallR != r || smallC != c)){
									count++;
								}

							}catch(ArrayIndexOutOfBoundsException e){
							}

						}
					}
					board[r][c].putClientProperty("state", count);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Beginner")){
			difficulty = 1;
		}
		if(e.getActionCommand().equals("Intermediate")){
			difficulty = 2;
		}
		if(e.getActionCommand().equals("Hard")){
			difficulty = 3;
		}

		if(difficulty == 1){
			dimR = 9;
			dimC = 9;
			numMines = 10;

		}
		if(difficulty == 2){
			dimR = 12;
			dimC = 12;
			numMines = 13;
		}
		if(difficulty == 3){
			dimR = 15;
			dimC = 15;
			numMines = 16;

		}
		if(e.getSource() == b && firstClick == false){

			timer.stop();
			timerLabel.setText("Timer: 0 sec");
			b.setIcon(smile);
			//createBoard(dimR, dimC);

		}
		createBoard(dimR, dimC);
		time = 0;
		menu.setText(e.getActionCommand());
	}

	public void mouseReleased(MouseEvent e){

		int row = (int)((JToggleButton)e.getComponent()).getClientProperty("row");
		int col = (int)((JToggleButton)e.getComponent()).getClientProperty("col");

		if(gameOn){
			if(e.getButton() == MouseEvent.BUTTON1 && board[row][col].getIcon() != flag){
				if(numSquaresLeft == numMines){
					timer.stop();
					b.setIcon(win);
					JOptionPane.showMessageDialog(null, "You Win!");
					gameOn = false;
				}

				if(firstClick){
					setBombsAndNums(row, col);
					numSquaresLeft--;
					firstClick = false;
					timer = new Timer(1000, el -> {
						time++;
						timerLabel.setText("Timer: "+time + " sec");
					});

					timer.start();
				}

				int state = (int)board[row][col].getClientProperty("state");

				if(state == -1){
					timer.stop();
					b.setIcon(dead);

					board[row][col].setContentAreaFilled(false);
					board[row][col].setOpaque(true);
					board[row][col].setBackground(Color.RED);
					board[row][col].setIcon(mines);
					board[row][col].setDisabledIcon(mines);
					board[row][col].setEnabled(false);
					for(int r = 0; r < board.length; r++){
						for(int c = 0; c < board[r].length; c++){
							int state1 = (int)board[r][c].getClientProperty("state");
							if(state1 == -1){
								board[r][c].setIcon(mines);
								board[r][c].setDisabledIcon(mines);

							}
							board[r][c].setEnabled(false);
						}
					}

					gameOn = false;
					JOptionPane.showMessageDialog(null, "You Lose!");

				}
				else{
					click(row, col);
				}
			}
			else if(e.getButton() == MouseEvent.BUTTON3){
				if(!board[row][col].isSelected() && board[row][col].getIcon() == null){
					board[row][col].setIcon(flag);
					board[row][col].setDisabledIcon(flag);
					board[row][col].setEnabled(false);
				}
				else if(board[row][col].getIcon() == flag){
					board[row][col].setIcon(null);
					board[row][col].setEnabled(true);
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e){
	}

	public void mousePressed(MouseEvent e){
	}

	public void mouseExited(MouseEvent e){
	}

	public void mouseEntered(MouseEvent e){
	}

	public void click(int row, int col){

		if(!board[row][col].isSelected()){
			board[row][col].setSelected(true);
		}

		int state = (int)board[row][col].getClientProperty("state");

		if(state != 0){
			if(board[row][col].getIcon() == null){
				board[row][col].setIcon(numIcons[state]);
				board[row][col].setDisabledIcon(numIcons[state]);
				board[row][col].setEnabled(false);
			}
		}
		else{
			for(int smallR = row-1; smallR <= row+1; smallR++){
				for(int smallC = col - 1; smallC <= col+1;  smallC++){
					try{
						if(!board[smallR][smallC].isSelected())
							click(smallR, smallC);
					}catch(ArrayIndexOutOfBoundsException e){

					}
				}
			}
		}
		numSquaresLeft--;
	}

	public static void main(String[]args){

		Minesweeper mines = new Minesweeper();

	}
}
