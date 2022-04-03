package threads;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServeurJeu extends Thread {
	
	private boolean isActive = true;
	private int nombreClients = 0;
	private int nombreSecret;
	private boolean fin;

	public static void main(String[] args) {
		
		new ServeurJeu().start();
	}
	
	@Override
	public void run() {
		
		try {
			
			ServerSocket ss = new ServerSocket(1236);
			nombreSecret = new Random().nextInt(1000);
			System.out.println("Le serveur a choisi son secret: "+nombreSecret);
			
			while(isActive){
				Socket socket = ss.accept();
				++nombreClients;
				new conversation(socket,nombreClients).start();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class conversation extends Thread{
		
		private Socket socket;
		private int numeroClient;
		
		public conversation(Socket socket, int numeroClient) {
			this.socket = socket;
			this.numeroClient = numeroClient;
		}
		
		@Override
		public void run() {
			
			try {
				
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is); 
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os,true);
		
				String ipClient = socket.getRemoteSocketAddress().toString();
				System.out.println("Connexion du client numéro "+numeroClient+" IP= "+ipClient);
				pw.println("Bienvenue, vous êtes le client numéro "+numeroClient);
				pw.println("Devinez le nombre secret.....?");
				
				while (true) {
					
					String req = br.readLine();
					int nombre = 0;
					boolean correctFormatRequest = false;
					
					try {
						
						nombre = Integer.parseInt(req);
						correctFormatRequest = true;
						
					} catch (NumberFormatException  e) {
						correctFormatRequest = false;
					}
					
					if (correctFormatRequest) {
						System.out.println("Client "+ipClient+" tentaive avec le nombre: "+nombre);
						if (fin == false) {
							if (nombre > nombreSecret) {
								pw.println("Votre nombre est supérieur au nombre secret");
							}else if(nombre < nombreSecret) {
								pw.println("Votre nombre est inférieur au nombre secret");
							}
							else {
								pw.println("BRAVO, vous avez gagné");
								System.out.print("BRAVO, au gagnant, Ip Client :"+ipClient);
								fin = true;
							}
						}else {
							pw.println("Jeu terminé, le gagnant est :"+ipClient);
						}
					}
					else {
						pw.println("Format de nombre incorrect");
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
