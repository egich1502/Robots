package model;

public class GameState {
    private final Robot robot;
    private RobotMovement robotCommand;

    public GameState(double robotX, double robotY, double robotDir){
        robot = new Robot(robotX, robotY, robotDir);
    }

    public void updateGameState(){
        if (robotCommand != null)
            robotCommand.onModelUpdateEvent(robot);
    }

    public void setRobotCommand(RobotMovement robotCommand) {
        this.robotCommand = robotCommand;
    }

    public Robot getRobot() {
        return this.robot;
    }
}
