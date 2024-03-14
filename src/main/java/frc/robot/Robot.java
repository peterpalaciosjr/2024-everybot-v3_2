package frc.robot;


import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends TimedRobot {

  private static final String kNothingAuto = "do nothing";
  private static final String kLaunchAndDrive = "launch drive";
  private static final String kLaunch = "launch";
  private static final String kDrive = "drive";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();


  PWMSparkMax leftRear, leftFront = new PWMSparkMax(1);
  //PWMSparkMax leftFront = new PWMSparkMax(1);
  PWMSparkMax rightRear, rightFront = new PWMSparkMax(2);
  //PWMSparkMax rightFront = new PWMSparkMax(2);
  PWMSparkMax m_launchWheel = new PWMSparkMax(3);
  PWMSparkMax m_feedWheel = new PWMSparkMax(4);
  PWMSparkMax m_rollerClaw = new PWMSparkMax(5);
  
  DifferentialDrive m_drivetrain;

  Joystick m_driverController = new Joystick(0);
  Joystick m_manipController = new Joystick(1);


  // --------------- Magic numbers. Use these to adjust settings. ---------------

 // Current Limits
  static final int DRIVE_CURRENT_LIMIT_A = 60;
  static final int FEEDER_CURRENT_LIMIT_A = 60;
  static final int LAUNCHER_CURRENT_LIMIT_A = 60;

// Speeds
  static final double LAUNCHER_BUMPER_SPEED = 1.0;
  //static final double LAUNCHER_AMP_SPEED = .25;
  static final double LAUNCHER_MIDFIELD_SPEED = 1.0;
  static final double LAUNCHER_INTAKE_SPEED = 1.0;
  static final double FEEDER_OUT_SPEED = 1.0;
  static final double FEEDER_IN_SPEED = -.7;
  static final double CLAW_OUTPUT_POWER = .5; 
  
  // Stall Power
  static final double CLAW_STALL_POWER = .1;
 

  
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("launch note and drive", kLaunchAndDrive);
    m_chooser.addOption("launch note and drive", kLaunchAndDrive);
    m_chooser.addOption("launch", kLaunch);
    m_chooser.addOption("drive", kDrive);
    SmartDashboard.putData("Auto choices", m_chooser);


    leftFront.setInverted(true);
    rightFront.setInverted(false);

    m_drivetrain = new DifferentialDrive(leftFront, rightFront);
   
    
    m_feedWheel.setInverted(true);
    m_launchWheel.setInverted(true);

    

   
    m_rollerClaw.setInverted(false);
    //m_climber.setInverted(false);

   
  }

 
  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Time (seconds)", Timer.getFPGATimestamp());
  }


  
  double AUTO_LAUNCH_DELAY_S;
  double AUTO_DRIVE_DELAY_S;

  double AUTO_DRIVE_TIME_S;

  double AUTO_DRIVE_SPEED;
  double AUTO_LAUNCHER_SPEED;

  double autonomousStartTime;

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();

   

    AUTO_LAUNCH_DELAY_S = 2;
    AUTO_DRIVE_DELAY_S = 4;

    AUTO_DRIVE_TIME_S = 1.5;
    AUTO_DRIVE_SPEED = -0.3;
    AUTO_LAUNCHER_SPEED = 1;
    
  
    if(m_autoSelected == kLaunch)
    {
      AUTO_DRIVE_SPEED = 0;
    }
    else if(m_autoSelected == kDrive)
    {
      AUTO_LAUNCHER_SPEED = 0;
    }
    else if(m_autoSelected == kNothingAuto)
    {
      AUTO_DRIVE_SPEED = 0;
      AUTO_LAUNCHER_SPEED = 0;
    }

    autonomousStartTime = Timer.getFPGATimestamp();
  }

  
  @Override
  public void autonomousPeriodic() {

    double timeElapsed = Timer.getFPGATimestamp() - autonomousStartTime;


    if(timeElapsed < AUTO_LAUNCH_DELAY_S)
    {
      m_launchWheel.set(AUTO_LAUNCHER_SPEED);
      m_drivetrain.arcadeDrive(0, 0);

    }
    else if(timeElapsed < AUTO_DRIVE_DELAY_S)
    {
      m_feedWheel.set(AUTO_LAUNCHER_SPEED);
      m_drivetrain.arcadeDrive(0, 0);
    }
    else if(timeElapsed < AUTO_DRIVE_DELAY_S + AUTO_DRIVE_TIME_S)
    {
      m_launchWheel.set(0);
      m_feedWheel.set(0);
      m_drivetrain.arcadeDrive(AUTO_DRIVE_SPEED, 0);
    }
    else
    {
      m_drivetrain.arcadeDrive(0, 0);
    }
    
  }


  @Override
  public void teleopInit() {
    
  
  }


  @Override
  public void teleopPeriodic() {

    
    if (m_manipController.getPOV() == 0) {
      m_launchWheel.set(-LAUNCHER_BUMPER_SPEED);
    }
    else if(m_manipController.getPOV() == 90)
    {
      m_launchWheel.set(-LAUNCHER_MIDFIELD_SPEED);
    }
    else if(m_manipController.getPOV() == 180)
    {
      //m_launchWheel.set(LAUNCHER_AMP_SPEED);
    }
    else if(m_manipController.getPOV() == 270)
    {
      m_launchWheel.set(LAUNCHER_INTAKE_SPEED);
      m_feedWheel.set(-FEEDER_IN_SPEED);
    }
    else 
    {
      m_launchWheel.set(0);
      m_feedWheel.set(0);
    }


    if (m_manipController.getRawButton(6))
    {
      m_feedWheel.set(-FEEDER_OUT_SPEED);
    }
    else if(m_manipController.getRawButtonReleased(6))
    {
      m_feedWheel.set(0);
    }


    if (m_manipController.getRawButton(5))
    {
      m_launchWheel.set(1.0);
    }
    else if(m_manipController.getRawButtonReleased(5))
    {
      m_launchWheel.set(0);
    }


    if(m_manipController.getRawButton(3))
    {
      m_rollerClaw.set(CLAW_OUTPUT_POWER);
    }
    else if(m_manipController.getRawButton(4))
    {
      m_rollerClaw.set(-CLAW_OUTPUT_POWER);
    }
    else
    {
      m_rollerClaw.set(0);
    }

      
      m_drivetrain.arcadeDrive(-m_driverController.getRawAxis(1), -m_driverController.getRawAxis(4), false);
  
  }
}

