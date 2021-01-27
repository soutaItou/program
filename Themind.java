
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
public class Themind extends JFrame implements ActionListener{
	Player my,enemy;
	//my:自分のカードに関する情報
	//enemy:相手のカードに関する情報
	JFrame gameframe,ruleframe;
	//gameframe;大元のフレーム
	//ruleframe:ルールの画面
	JPanel bpanel,startpanel,selectpanel,gamepanel,misspanel,successpanel,turnpanel,cardpanel1,numpanel,cardpanel2,hintpanel,waitingpanel,answerpanel1,answerpanel2;
	//bpanel:CardLayoutを実装するためにFrameの上にとりあえずおくパネル
	//startpanel:ゲームの設定などをするパネル
	//selectpanel:ゲームの難易度を設定するパネル
	//gamepanel:ゲームをするパネル
	//misspanel:間違えた時に表示されるパネル
	//successpanel:成功した時に表示されるパネル
	//turnpanel:現在のターンや相手、自分の手札を表示するパネル
	//cardpamel1:相手の手札を表示するパネル
	//cardpanel2:自分の手札を表示するパネル
	//numpanel:場のカードを表示するパネル
	//hintpanel:ヒントに関する情報を表示するパネル
	//waitingpanel:gameを押した後の相手の応答を待っている時のパネル
	//answerpanel1:答えの時の相手の手札のパネル
	//answerpanel2:答えの時の自分の手札のパネル
	CardLayout layout;
	int turnnum=1,card,maxnum=70,hinttimes=3,hintnum;
	//turnnum:現在のターン数
	//card:配布される手札の数
	//maxnum:手札の数字の上限　70なら0-69
	//hinttimes:ヒントを使える回数
	//hintnum:ヒントの数字
	JLabel turn,card1,card2,readylabel,hintlabel,waitinglabel,result,startlabel,rulelabel;
	//turn:現在のターン数のラベル
	//card1:相手の手札の数のラベル
	//card2:自分の手札の数のラベル
	//readylabel:準備ができましたラベル
	//hintlabel:ヒントを表示するラベル
	//waitinglabel:gameを押した後の相手の応答を待っている時に表示されるラベル
	//result:gameの勝敗を表示するラベル
	//startlabel:スタート画面のラベル
	//rulelabel:ルール画面のラベル
	int myplayer[],answercard[];
	//myplayer:この配列が１となっている時の添え字が自分の手札の数字
	//answercard:答え手札のカードの値
	ImageIcon behindIcon,usedIcon,startIcon,ruleIcon;
	//behindIcon:トランプ上側のアイコン
	//usedIcon:使用済みトランプのアイコン
	//startIcon:スタート画面の画像のアイコン
	//ruleIcon:ルール画面のアイコン
	
	String msg,cards,maxnums;
	//msg:メッセージ送信する時の変数
	//cards:そのゲームで使用するカードの枚数を文字列に変換するための変数
	//maxnums:そのゲームで使用する数の上限を文字列に変換するための変数
	
	JButton start,button1[],button2[],buttonmy[],buttonenemy[],fieldcard,ready1,ready2,ready3,menu,hint,rulebutton;
	//start:ゲームpanelへ移行するボタン
	//button1[]:相手のカード
	//button2[]:自分のカード
	//buttonmy[]:答えの時に表示する自分のカード
	//buttonenemy[];答えの時に表示する自分のカード
	//fieldcard:現在の場のかーど
	//ready1:startpanelの準備ができた時に押すボタン,ゲームの難易度優しい
	//ready2:startpanelの準備ができた時に押すボタン,ゲームの難易度普通
	//ready3:startpanelの準備ができた時に押すボタン,ゲームの難易度難しい
	//menu:startpanelへ飛ぶボタン
	//hint;ヒントを表示するボタン
	//rulebutton:ルール画面を表示するボタン
	boolean answerflag=true,waitingflag=true,prepflag=true;
	//answerflag:最初のみanswercardの配列を作るためのフラグ
	//waitingflag:プログラムを相手が開始しているかどうかのフラグ
	//prepflag:準備ができましたを表示するフラグ
	PrintWriter out;
	//out:出力用のライター
	Clip clip;
	

	public Themind(){
		//Playerクラスのmy,enemyを生成
		my=new Player();
		enemy=new Player();
		my.name = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);//ダイアログボックスを表示させ、名前を取得
		if(my.name.equals("")){
			my.name = "Noname";//名前がないときは,"Noname"とする
		}
		my.server = JOptionPane.showInputDialog(null,"serverのIPアドレスを入力してください","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);//ダイアログボックスを表示させ、IPアドレスを取得
		if(my.server.equals("")){
			my.server = "localhost";//IIPアドレスがないときは，"localhost"とする
		}
		Socket socket=null;
		try {
			//"localhost"は，自分内部への接続．l
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket(my.server, 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}
		MesgRecvThread mrt = new MesgRecvThread(socket, my.name);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
		syori();	//syori()メソッドを実行する
	}
	
	public void syori(){		
		gameframe=new JFrame();//gameframeを生成
		gameframe.setLayout(new BorderLayout());//gameframeのlayoutをボーダーレイアウトにする
		gameframe.setTitle("Themind");//gameframeのタイトル名をThemindにする
		gameframe.setSize(750,550);//gameframeのサイズを設定する
		gameframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//gameframeの閉じるボタンが押されたらアプリケーションを終了するようにする
		
		ruleframe=new JFrame();//ruleframeを生成
		bpanel=new JPanel();//bpanelを生成
		layout = new CardLayout();//cardlayoutを生成
		bpanel.setLayout(layout);//bpanelのレイアウトをlayoutに設定
		gamepanel=new JPanel();//gamepanelを生成
		gamepanel.setLayout(new BorderLayout());//gamepanelのレイアウトをボーダーレイアウトに設定
		startpanel=new JPanel();//startpanelを生成
		startpanel.setLayout(new BorderLayout());//startpanelのレイアウトをボーダーレイアウトに設定
		selectpanel=new JPanel();//selectpanelを生成
		misspanel=new JPanel();//misspanelを生成
		successpanel=new JPanel();//successpanelを生成
		waitingpanel=new JPanel();//waitingpanelを生成
		waitingpanel.setLayout(new BorderLayout());//watitingpanelのレイアウトをボーダーレイアウトに設定
		turnpanel=new JPanel();//turnpanelを生成
		cardpanel1=new JPanel();//cardpanel1を生成
		numpanel= new JPanel();//numpanelを生成
		cardpanel2= new JPanel();//cardpanel2を生成
		hintpanel=new JPanel();//hintpanelを生成
		hintpanel.setLayout(new BorderLayout());//hintpanelのレイアウトをボーダーレイアウトに設定
		gameframe.add(bpanel);//gameframeにbpanelを追加
		bpanel.add(startpanel,"start");//bpanelにstartpanelを追加 panel名をstartにする
		bpanel.add(gamepanel,"game");//bpanelにgamepanelを追加 panel名をgameにする
		bpanel.add(misspanel,"miss");//bpanelにmisspanelを追加 panel名をmissにする
		bpanel.add(successpanel,"success");//bpanelにsuccesspanelを追加 panel名をsuccessにする
		bpanel.add(waitingpanel,"waiting");//bpanelにwaitingpanelを追加 panel名をwaitingにする
		startpanel.add(selectpanel,BorderLayout.NORTH);//startpanelの北側にselectpanelを配置
		selectpanel.setOpaque(true);//selectpanelの全てのピクセルをペイント
    	selectpanel.setBackground(Color.BLACK);//selectpanelの背景を黒にする
		waitinglabel=new JLabel(new ImageIcon("waiting.png"));//watitinglabelを生成　画像はwaiting.pngから取得
		waitingpanel.add(waitinglabel,BorderLayout.CENTER);//watingpanelの真ん中にwaitinglabelを配置
		waitingpanel.setOpaque(true);//watingpanelの全てのピクセルをペイント
   		waitingpanel.setBackground(Color.BLACK);//waitingpanelの背景を黒にする
		turn=new JLabel("現在"+turnnum+"ターン目",JLabel.CENTER);//turnを生成 
		turn.setFont(new Font("Arial",Font.PLAIN,30));//turnのフォントを設定
		turn.setForeground(Color.WHITE);//turnの色を白に設定

		card1=new JLabel(enemy.name+"の手札:"+enemy.cardnum+"枚",JLabel.CENTER );//card1を生成
		card1.setFont(new Font("Arial",Font.PLAIN,20));//card1のフォントを設定
		card1.setForeground(Color.CYAN);//card1の色をシアンに設定
		card2=new JLabel(my.name+"の手札:"+my.cardnum+"枚",JLabel.CENTER );//card2を生成
		card2.setFont(new Font("Arial",Font.PLAIN,20));//card2のフォントを設定
		card2.setForeground(Color.RED);//card2の色を赤に設定
		turnpanel.setLayout(new BorderLayout());//turnpanelのレイアウトをボーダーレイアウトに設定
		numpanel.setLayout(new BorderLayout());//numpanelのレイアウトをボーダーレイアウトに設定
		gamepanel.add(turnpanel,BorderLayout.NORTH);//gamepanelの北にturnpanelを配置
		gamepanel.add(cardpanel1,BorderLayout.WEST);//gamepanelの西にcardpanel1を配置
		gamepanel.add(numpanel,BorderLayout.CENTER);//gamepanelの真ん中にnumpanelを配置
		gamepanel.add(cardpanel2,BorderLayout.EAST);//gamepanelの東にcardpanel2を配置
		gamepanel.add(hintpanel,BorderLayout.SOUTH);//gamepanelの南にhintpanelを配置
		startlabel=new JLabel();//startlabelを生成
		rulelabel=new JLabel();//rulelabelを生成
		startIcon=new ImageIcon("start.jpeg");//startIconを生成　start.jpegから取得
		ruleIcon=new ImageIcon("rule.png");//ruleIconを生成　rule.jpegから取得
		startlabel.setIcon(startIcon);//startlabelにstartIconを設定
		start=new JButton("start"); //startボタンを生成　アクションリスナーを設定 アクションコマンドはgame
		start.addActionListener(this);
    	start.setActionCommand("game");
    	rulebutton=new JButton("ルールを見る");//ruleボタンを生成　アクションリスナーを設定 アクションコマンドはrule
    	rulebutton.addActionListener(this);
    	rulebutton.setActionCommand("rule");
    	ready1=new JButton("難易度:易しい");//ready1ボタンを生成　アクションリスナーを設定rアクションコマンドはready1
    	ready1.addActionListener(this);
    	ready1.setActionCommand("ready1");
    	ready2=new JButton("難易度:普通");//ready2ボタンを生成　アクションリスナーを設定rアクションコマンドはready2
    	ready2.addActionListener(this);
    	ready2.setActionCommand("ready2");
    	ready3=new JButton("難易度:難しい");//ready3ボタンを生成　アクションリスナーを設定rアクションコマンドはready3
    	ready3.addActionListener(this);
    	ready3.setActionCommand("ready3");
    	//selectpanelにrulubutton ready1 ready2 ready3 を配置
    	selectpanel.add(rulebutton);
    	selectpanel.add(ready1);
    	selectpanel.add(ready2);
		selectpanel.add(ready3);
		
		startpanel.add(startlabel,BorderLayout.SOUTH);//startpanelの南にstartlabelを配置
		layout.show(bpanel,"waiting");//watitinglabelを表示
		
		
		//もし自分がMyServerに接続した二人目のプレイヤーなら１人目のプレイヤーが待っているので、プレイヤー名と準備ができたことを送り、startpanelを表示
		if(my.number==2){
			waitingflag=false;
			msg="startprogram"+" "+my.name;
			out.println(msg);
			out.flush();
			layout.show(bpanel,"start");
		}
	gameframe.setVisible(true);// gameframeを可視化
	}
	public void play(){
		
		//hintlabelを生成、　フォントを設定　 色を白に設定
		hintlabel=new JLabel("ヒント:  　残り使用可能回数:"+hinttimes);
		hintlabel.setFont(new Font("Arial",Font.PLAIN,20));
		hintlabel.setForeground(Color.WHITE);
		//behindIcon usedIconを生成　behind.jpeg　とused.jpegから取得
		behindIcon=new ImageIcon("behind.jpeg");
		usedIcon=new ImageIcon("used.jpeg");
		//hintpanelの全てのピクセルをペイントに設定　黒色に設定　hintpanelの西にhintlabelを配置
		hintpanel.setOpaque(true);
		hintpanel.setBackground(Color.BLACK);
		hintpanel.add(hintlabel,BorderLayout.WEST);
		//turnpanelに　西　真ん中　東　の順に　card1 turn card2 を配置
		//turnpanelの全てのピクセルをペイントに設定　黒色に設定
		turnpanel.add(card1,BorderLayout.WEST);
		turnpanel.add(turn,BorderLayout.CENTER);
		turnpanel.add(card2,BorderLayout.EAST);
		turnpanel.setOpaque(true);
   		turnpanel.setBackground(Color.BLACK);
   		//myplayer my.card button1 button2 fieldcard　の配列を生成
		myplayer=new int[maxnum];
		my.card=new int[card];
		button1=new JButton[card];
		button2=new JButton[card];
		fieldcard=new JButton();
		//menuを生成　アクションリスナーを設定　アクションコマンドをmenu　にする
		menu=new JButton("メニューへ");
		menu.addActionListener(this);
		menu.setActionCommand("menu");
		//hint を生成　アクションリスナーを設定　アクションコマンドをhintにする　 hintpanelの真ん中に配置
		hint=new JButton("ヒント");
		hint.addActionListener(this);
		hint.setActionCommand("hint");
		hintpanel.add(hint,BorderLayout.CENTER);
		//numpanelにfieldcardを配置　numpanelの全てのピクセルをペイントに設定　色を黒にする
		numpanel.add(fieldcard);
		numpanel.setOpaque(true);
		numpanel.setBackground(Color.BLACK);
		//myplayerを初期化
		for(int i=0;i<maxnum;i++){
			myplayer[i]=0;
		}
		//ランダムな数値を自分の手札に配布　もし被ったらもう一度ランダムな数値を代入 mynumberに応じて偶数奇数で配布している
		Random rand=new Random();
		int num=0;
		my.cardnum=card;
		for(int i=0;i<card;i++){
			num = rand.nextInt((maxnum/2)-1)*2+my.number;
			while(true){
				if(myplayer[num]==0){
					myplayer[num]=1;
					break;
				}else{
					num=rand.nextInt(maxnum/2-1)*2+my.number;
				}
			}
		}
		//cardpanel1,cardpanel2をグリッドレイアウトに設定して横幅が4 枚となるように配置
		//cardpanel1 cardpanel2　の全てのピクセルをペイントに設定　色は黒にする
		int height=card/4;
		if(card%4!=0){
			height+=1;			
		}
		System.out.println(height);
		my.cardnum=0;
		cardpanel2.setLayout(new GridLayout(height,4));
		cardpanel1.setLayout(new GridLayout(height,4));
		cardpanel1.setOpaque(true);
		cardpanel1.setBackground(Color.BLACK);
		cardpanel2.setOpaque(true);
		cardpanel2.setBackground(Color.BLACK);
		//MyServerに送るメッセージ　最初の手札の情報であること
		//どのプレイヤーの手札であるか、カードの枚数を入れる
		//自分の手札の数字を決めた配列を元に、自分と相手の手札 button1 button2 をcardpanel1 cardpanel2に配置する
		String msg="firstcard"+" "+my.name+" "+card;
		for(int i=0;i<maxnum;i++){
			if(myplayer[i]==1){
				//msgに自分の手札の数字を小さい順に追加していく
				String a=""+i;
				msg+=" "+a;
				button1[my.cardnum]=new JButton(behindIcon);
				button2[my.cardnum]=new JButton(a);
				button2[my.cardnum].addActionListener(this);
				my.card[my.cardnum]=i;
				String s =""+my.cardnum;
    			//button2のアクションコマンドは数字の値とする
    			button2[my.cardnum].setActionCommand(s);
    			button1[my.cardnum].setPreferredSize(new Dimension(60, 38));
    			button2[my.cardnum].setPreferredSize(new Dimension(60, 38));
    			button2[my.cardnum].setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 30));
       			cardpanel1.add(button1[my.cardnum]);
    			cardpanel2.add(button2[my.cardnum]);
    			
    			my.cardnum+=1;
    			}
    			
		}
		//自分と相手のカード数は同じ
		enemy.cardnum=my.cardnum;
		//card1 とcard2 に今の手札の数を表示するように設定
		card1.setText(enemy.name+"の手札:"+enemy.cardnum+"枚");
		card2.setText(my.name+"の手札:"+my.cardnum+"枚" );
		repaint();
		//先ほどのmsgをMyServerに送る
		out.println(msg);
		out.flush();
			
	}
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myname;
		//socket:通信用のソケット
		//myname:自分の名前
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myname = n;
		}
		
		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				//socketからデータを１行ごとにまとめて受け取れるように生成する。
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myname);//接続の最初に名前を送る
				while(true) {
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						switch(cmd){
						//相手がプログラムをMyServerと接続してプログラムを開始した時の処理
						case"startprogram":
							//もし相手がプログラムを開始したらスタートパネルを表示
							if(!(inputTokens[1].equals(my.name))){
								layout.show(bpanel,"start");
								gameframe.setVisible(true);
							}
							break;
						//各プレイヤーが難易度を選択したかの処理
						case "ready":
							//自分が選択した時、card,maxnumを選んだ難易度の値にする。
							if(inputTokens[1].equals(my.name)){
								System.out.println("私が準備できました。");
								int i=Integer.parseInt(inputTokens[2]);
								System.out.println("aaaaaaa:"+i);
								card=i;
								maxnum=Integer.parseInt(inputTokens[3]);
								repaint();
							 }//相手が難易度を選択した時card,maxnumを上書きできるようにする。
							 else{
							 	enemy.readyflag=true;
							 	enemy.name=inputTokens[1];
							 	int i=Integer.parseInt(inputTokens[2]);
								card=i;
								maxnum=Integer.parseInt(inputTokens[3]);
							 	System.out.println("相手が準備できました");
							 	repaint();
							 	
							}//お互いが準備できた時readylabelを表示してstartボタンが表示できる			
							//prepflagがないと、難易度ボタンを押すたびにstartボタンが追加される			
							if(enemy.readyflag==true&&my.readyflag==true){
								if(prepflag){
									readylabel=new JLabel("準備ができました");
									 readylabel.setForeground(Color.WHITE);
									selectpanel.add(start);
							 		selectpanel.add(readylabel);
							 		repaint();
							 		selectpanel.setVisible(true);
							 		gameframe.setVisible(true);
							 		prepflag=false;
							 	}
						 }
							 break;
							 //各プレイヤーがstartボタンを押した場合の処理
						case"gamewaiting":
							//自分がstartボタンを押したら、自分のflagをtrueにして、接続音をならす.
							if(inputTokens[1].equals(my.name)){
								my.gamewaitingflag=true;
								clip = createClip(new File("communication.wav"));
								
								try {
								clip.loop(Clip.LOOP_CONTINUOUSLY);
 		 						Thread.sleep(1000);
 		 						
								} catch (InterruptedException ae) {
								}
							}//相手がstartボタンを押したら、相手のflagをtrueにする
							else if(inputTokens[1].equals(enemy.name)){
								enemy.gamewaitingflag=true;
							}//相手も自分もflagがtrueなら、音声再生を停止して、play()処理をして、gamepanelを表示
							if(my.gamewaitingflag==true&&enemy.gamewaitingflag==true){
								clip.close();
								play();
								layout.show(bpanel,"game");
								my.gamewaitingflag=false;
								enemy.gamewaitingflag=false;
								//接続が成功した時に接続成功音を鳴らす
								clip = createClip(new File("start.wav"));
								
									try {
										clip.start();
 		 								Thread.sleep(2000);
 		 								clip.close();
									} catch (InterruptedException e) {
									}

							}
							break;
						//手札が確定した時の処理
						case "firstcard":
							//相手の手札の配列の数と値が送られてくるので順番にenemy.cardの配列に入れる
							if(inputTokens[1].equals(enemy.name)){
								int num=Integer.parseInt(inputTokens[2]);
								System.out.println(inputTokens[2]);
								enemy.card=new int[num];
								enemy.cardnum=card;
								for(int i=0;i<card;i++){
									enemy.card[i]=Integer.parseInt(inputTokens[i+3]);
								}

							}
							break;
							//接続した時にその順番の番号を送られてきた時の処理
						case "Mynumber":
							my.number=Integer.parseInt(inputTokens[1]);
							break;
							//現在場に出されたカードの処理
						case "nowcard":
							//最初だけ答えを書くプレイヤーが知っておく必要があるので、answerの配列を作る
							while(answerflag){
								answercard=new int[card*2];
								//配列を一つにまとめる
								System.arraycopy(my.card,0,answercard,0,card);
								System.arraycopy(enemy.card,0,answercard,card,card);
								//配列を小さい順にソートする
								Arrays.sort(answercard);
								System.out.println("answercard: " + Arrays.toString(answercard));
								//whileを抜けるためにanswerflagをfalseにする。
								answerflag=false;
								hintnum=answercard[0];
							}
							//turnnumを進める
							turnnum+=1;
							//自分のカードが押されたら、自分のカードの数を減らす
							if(inputTokens[1].equals(my.name)){
								my.cardnum-=1;
							}//相手のカードが押されたら相手のカードの数を減らす
							else if(inputTokens[1].equals(enemy.name)){
								enemy.cardnum-=1;
								//相手のカードが押されたら使用済みアイコンにする
								button1[enemy.cardnum].setIcon(usedIcon);
							}
							String s = inputTokens[2];
							int num=Integer.parseInt(s);
							//もし押されたカードが正しければそのまま進む
							if(num==answercard[card*2-1-(my.cardnum+enemy.cardnum)]){
								System.out.println(card*2-(my.cardnum+enemy.cardnum));
								if(my.cardnum+enemy.cardnum>1){
								hintnum=answercard[card*2-(my.cardnum+enemy.cardnum)];
								}//残りのカードが０枚なら成功画面へ
								//成功音を出しながsuccesspanelを表示
								if((my.cardnum+enemy.cardnum)==0){
									System.out.println("成功画面へ");
									showanswer("success");
									layout.show(bpanel,"success");
									reset();
									clip = createClip(new File("success.wav"));
								
									try {
										clip.start();
 		 								Thread.sleep(3000);
 		 								clip.close();
									} catch (InterruptedException e) {
									}
								}
							}else{
								//もし押されたカードが間違っていたら、								
								//失敗音を出しながらmisspanelを表示
								showanswer("miss");
								layout.show(bpanel, "miss");
								reset();
								clip = createClip(new File("miss.wav"));
								
									try {
										clip.start();
 		 								Thread.sleep(3000);
 		 								clip.close();
									} catch (InterruptedException e) {
									}

							}
							//もし押されたカードが正しければ、turn,card1,card2を書き換える
							String tnum=""+turnnum;
							turn.setText("現在"+tnum+"ターン目");
							turn.setForeground(Color.WHITE);

							card1.setText(enemy.name+"の手札:"+enemy.cardnum+"枚");
							
							card2.setText(my.name+"の手札:"+my.cardnum+"枚");
							fieldcard.setText(s);
							fieldcard.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 150));
							repaint();
							break;
							}
					}else {
						break;
					}
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}

	
		
	public static void main(String[] args){
		Themind themind= new Themind();
		//themindを生成する
	}
	//押されたカードが全て正しいもしくは間違えたカードが押された時に使うメソッド
	public void showanswer(String s){
		//answerpanel1,answerpanel2を生成して横４枚となるようにグリッドレイアウトに設定
		answerpanel1=new JPanel();
		answerpanel2=new JPanel();
		int height1=card/4;
		if(card%4!=0){
			height1+=1;			
		}
		answerpanel1.setLayout(new GridLayout(height1,4));
		answerpanel2.setLayout(new GridLayout(height1,4));
		buttonmy=new JButton[card];
		buttonenemy=new JButton[card];

		//相手と自分のカードのボタンを表示
		for(int i=0;i<card;i++){
				String a =""+my.card[i];
				String b=""+enemy.card[i];
				buttonmy[i]=new JButton(a);
				buttonenemy[i]=new JButton(b);
				buttonmy[i].setPreferredSize(new Dimension(80, 80));
				buttonenemy[i].setPreferredSize(new Dimension(80, 80));
				buttonmy[i].setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 50));
				buttonenemy[i].setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 50));
				answerpanel1.add(buttonenemy[i]);
				answerpanel2.add(buttonmy[i]);	
		}
		
		//失敗した時の処理
		if(s.equals("miss")){
			//resultを生成　resultを白色に設定　misspanelをボーダーレイアウトに設定
			//misspanelに北　西　東　南　の順に　result answerpanel1 answerpanel2 menu　の順に配置します
			//misspanel answerpanel1 answerpanel2 の全てのピクセルをペイントに設定　赤色にする
			result=new JLabel("結果は失敗です",JLabel.CENTER);
			result.setForeground(Color.WHITE);
			result.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 70));
			misspanel.setLayout(new BorderLayout());
			misspanel.add(result,BorderLayout.NORTH);
			misspanel.add(answerpanel1,BorderLayout.WEST);
			misspanel.add(answerpanel2,BorderLayout.EAST);
			misspanel.add(menu,BorderLayout.SOUTH);
			misspanel.setOpaque(true);
   		 	misspanel.setBackground(Color.RED);
   		 	answerpanel1.setOpaque(true);
   		 	answerpanel1.setBackground(Color.RED);
   		 	answerpanel2.setOpaque(true);
   		 	answerpanel2.setBackground(Color.RED);

			
		}
		//成功した時の処理
		else if(s.equals("success")){
			//resultを生成　resultをシアンに設定　
			//successpanelをボーダーレイアウトに設定　
			//successpanelに 北　西　東　南　から順に　result answerpanel1 answerpanel2 menu　を配置
			//successpanel answerpanel1 answerpanel2 の全てのピクセルをペイントに設定　オレンジ色にする

			result=new JLabel("結果は成功です",JLabel.CENTER);
			result.setForeground(Color.CYAN);
			result.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 70));
			successpanel.setLayout(new BorderLayout());
			successpanel.add(result,BorderLayout.NORTH);
			successpanel.add(answerpanel1,BorderLayout.WEST);
			successpanel.add(answerpanel2,BorderLayout.EAST);
			successpanel.add(menu,BorderLayout.SOUTH);
			successpanel.setOpaque(true);
   		 	successpanel.setBackground(Color.ORANGE);
   		 	answerpanel1.setOpaque(true);
   		 	answerpanel1.setBackground(Color.ORANGE);
   		 	answerpanel2.setOpaque(true);
   		 	answerpanel2.setBackground(Color.ORANGE);

		}
		result.setFont(new Font("Arial",Font.PLAIN,20));
		repaint();
		
	}	
	//パネルに載っているコンポーネントやフラグの設定などをリセットするメソッド
	public void reset(){
		  //startpanel selectpanel turnpanel cardpanel1 cardpanel2 numpanel hintpanelのコンポーネントを全て取り除く
		  //flagを全て初期状態にする/
		  //数値を全て初期状態にする。
			startpanel.removeAll();
			selectpanel.removeAll();
			turnpanel.removeAll();
			cardpanel1.removeAll();
			cardpanel2.removeAll();
			numpanel.removeAll();
			hintpanel.removeAll();
			my.readyflag=false;
			enemy.readyflag=false;
			answerflag=true;
			prepflag=true;

			hintnum=0;
			hinttimes=3;
			enemy.cardnum=0;
			my.cardnum=0;
			turnnum=1;
			card=0;
		}
		//音声ファイルを扱うメソッド
		public static Clip createClip(File path) {
		//指定されたURLのオーディオ入力ストリームを取得
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
			
			//ファイルの形式取得
			AudioFormat af = ais.getFormat();
			
			//単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			
			//指定された Line.Info オブジェクトの記述に一致するラインを取得
			Clip c = (Clip)AudioSystem.getLine(dataLine);
			
			//再生準備完了
			c.open(ais);
			
			return c;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
		switch (cmd){
		//ready1が押された時の処理
		case"ready1":
			//misspanel suceesspanel のコンポーネントを取り除く,flagをtrueにする
			misspanel.removeAll();
			successpanel.removeAll();
			my.readyflag=true;
			//card maxnumを設定
			card=5;
			maxnum=20;
			//maxnum --- maxnums   card----cards  int型をString型に変換
			maxnums=""+maxnum;
			cards=""+card;
			//MyServerに送るメッセージ  
			msg="ready"+" "+my.name+" "+cards+" "+maxnums;
			out.println(msg);
			out.flush();
			break;
			//ready2が押された時の処理
		case"ready2":
			//misspanel suceesspanel のコンポーネントを取り除く,flagをtrueにする
			misspanel.removeAll();
			successpanel.removeAll();
			my.readyflag=true;
			//card maxnumを設定
			card=10;
			maxnum=50;
			//maxnum --- maxnums   card----cards  int型をString型に変換
			maxnums=""+maxnum;
			cards=""+card;
			//MyServerに送るメッセージ  
			msg="ready"+" "+my.name+" "+cards+" "+maxnums;
			out.println(msg);
			out.flush();
			break;
			//ready3が押された時の処理
		case"ready3":
			//misspanel suceesspanel のコンポーネントを取り除く,flagをtrueにする
			misspanel.removeAll();
			successpanel.removeAll();
			my.readyflag=true;
			//card maxnumを設定
			card=15;
			maxnum=70;
			//maxnum --- maxnums   card----cards  int型をString型に変換
			maxnums=""+maxnum;
			cards=""+card;
			//MyServerに送るメッセージ  
			msg="ready"+" "+my.name+" "+cards+" "+maxnums;
			out.println(msg);
			out.flush();
			break;
			 //gameボタンが押された時の処理
		case"game":
			//watingpanelを表示
			layout.show(bpanel, "waiting");
			repaint();
			//MyServerにwatingしているプレイヤーの名前送る
			msg="gamewaiting"+" "+my.name;
			out.println(msg);
			out.flush();
			break;
		case"used":
			break;
		case"else":
			break;		
			//ruleボタンが押された時の処理
		case"rule":
			//ruleframeを生成　ボーダーレイアウトに設定　rulelabelを追加
			ruleframe.setLayout(new BorderLayout());
			ruleframe.setTitle("ルール");
			ruleframe.setSize(510,280);
			rulelabel.setIcon(ruleIcon);
			ruleframe.add(rulelabel,BorderLayout.CENTER);
			ruleframe.setVisible(true);
			//menuボタンが押された時の処理
		case "menu":
			//selectpanelにrulebutton ready1 ready2 ready3 を追加
			//startpamelにselectpanel startlabelを追加
			selectpanel.add(rulebutton);
			selectpanel.add(ready1);
			selectpanel.add(ready2);
			selectpanel.add(ready3);
			selectpanel.setOpaque(true);
    		selectpanel.setBackground(Color.BLACK);
			startlabel.setIcon(startIcon);
			startpanel.add(selectpanel,BorderLayout.NORTH);
			startpanel.add(startlabel,BorderLayout.SOUTH);
			//startpanelを表示
			layout.show(bpanel,"start");
			break;
			//hintボタンが押された時の処理
		case "hint":
			//ヒントの残り回数が0より大きければヒント音とともに表示
				if(hinttimes>0){
					hinttimes-=1;
					int s=hintnum/10;
					clip = createClip(new File("hint.wav"));
					try {
						clip.start();
 		 				Thread.sleep(1000);
 		 				clip.close();
					} catch (InterruptedException ae) {
					}
					 
					hintlabel.setText("ヒント:10の位は "+s+"です   ヒント使用可能回数:"+hinttimes);
					repaint();
				}//ヒントを使い切っていれば,使い切りましたと表示
				else{
					hintlabel.setText("使い切りました");
					repaint();
				}
				break;
				//自分のカードが押された時の処理
			default:
				//押されたカードの情報をMyServerに送る
				//押されたカードのアイコンを使用済みアイコンにする。
				int n=Integer.parseInt(cmd);
				String s =button2[n].getText();
				msg="nowcard"+" "+my.name+" "+s;
				out.println(msg);
				out.flush();
				button2[n].setActionCommand("used");
				button2[n].setIcon(usedIcon);
 				repaint();
 			}
		

	}
	
}