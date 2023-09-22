package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  WARNING: Most likely doesn't work if need to access any subdirectories of CWD as of now
 *  @author Owen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        if (firstArg.equals("init")) {
            if (Repository.GITLET_DIR.exists()) {
                // May have to switch to another method to check whether init() has been called
                // Not sure if this works with nested directories in your CWD
                Utils.message("A Gitlet version-control system already exists " +
                        "in the current directory.");
                System.exit(0);
            }
            validateNumArgs(args, 1);
            Repository.init();
        } else {
            if (!Repository.GITLET_DIR.exists()) {
                Utils.message("Not in an initialized Gitlet directory.");
                System.exit(0);
            } else {
//                //Sneaky debugging
//                if (Repository.getHeadId() == null) {
//                    System.out.println("WARNING: Previous commands made headId null");
//                    String commands = Utils.readContentsAsString(Repository.COMMANDSFILE);
//                    System.out.println(commands);
//                }
                execute(args);
//                //Sneaky debugging tool
//                String commands = Utils.readContentsAsString(Repository.COMMANDSFILE);
//                for (String s : args) {
//                    commands = commands + s + " ";
//                }
//                commands = commands + System.lineSeparator();
//                Utils.writeContents(Repository.COMMANDSFILE, commands);
            }
        }
    }

    /**
     * Checks the number of arguments versus the expected number
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }

    public static void execute(String[] args) {
        switch (args[0]) {
            case "add":
                validateNumArgs(args, 2);
                Repository.addFile(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else if (args.length == 3 && args[1].equals("--")) {
                    //checkout -- [file name]
                    Repository.checkout(Repository.getHeadId(), args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    // checkout [commit id] -- [file name]
                    Commit c = Commit.fromFile(args[1]);
                    Repository.checkout(args[1], args[3]);
                } else {
                    Utils.message("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log(Repository.getHeadId());
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.gLog();
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.remove(args[1]);
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "branch":
                validateNumArgs(args, 2);
                Repository.createBranch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                validateNumArgs(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
        }
    }
}
