public class Client
{
    public static void main(String[] args) {
        // Player player = new Player();
        Thread playerThread = new Thread(new Player());
        playerThread.start();
    }
}
