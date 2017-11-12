public class ManagementServer // implements Runnable
{
    public static void main(String[] args) {
        // GameServer gs = new GameServer();
        Thread gameServerThread = new Thread(new GameServer());
        gameServerThread.start();
    }
}
