package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/** Represents a gitlet repository. Contains most of the methods.
 *
 *  @author Owen
 */
public class Repository {
    // I'll put the tree structure in repository for now
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     *
     * REMEMBER: Initialize files in init() for all new variables that need persistence
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The blobs directory. */
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    /** File saving map of files staged for addition. */
    public static final File ADDMAPFILE = join(GITLET_DIR, "addMap");
    /** File saving map of branch names/commit ids. */
    // Change this to a set of branch objects/just directly navigate the BRANCHES dir?
    public static final File BRANCHMAPFILE = join(GITLET_DIR, "branchMap");
    /** File saving set of files staged for removal. */
    public static final File RMSETFILE = join(GITLET_DIR, "rmSet");
    /** File saving the head id. */
    public static final File CURRENTBRANCHFILE = join(GITLET_DIR, "currentBranch");
//    /** Sneaky persistence for debugging */
//    public static final File COMMANDSFILE = join(GITLET_DIR, "commands");

    public static final SimpleDateFormat SDF = new
            SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
//    static final SimpleDateFormat SDF = new SimpleDateFormat(" EEE MMM d HH:mm:ss yyyy Z");
//    /** List of files in CWD. */
////    public static List<String> fileNames;
    public static void makeFile(File f) {
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        // Initialize folders
        GITLET_DIR.mkdir();
        Commit.COMMITS.mkdir();
        BLOBS.mkdir();
        // make first commit
        Commit c1 = new Commit();
        c1.toFile();
        //
        makeFile(CURRENTBRANCHFILE);
        writeContents(CURRENTBRANCHFILE, "master");
        //
        makeFile(ADDMAPFILE);
        TreeMap<File, String> addMap = new TreeMap<>();
        writeObject(ADDMAPFILE, addMap);
        //
        makeFile(RMSETFILE);
        TreeSet<File> rmSet = new TreeSet<>();
        writeObject(RMSETFILE, rmSet);
        //
        makeFile(BRANCHMAPFILE);
        TreeMap<String, String> branchMap = new TreeMap<>();
        branchMap.put("master", c1.getId());
        writeObject(BRANCHMAPFILE, branchMap);
        //
//        makeFile(COMMANDSFILE);
//        writeContents(COMMANDSFILE, "Commands entered:" + System.lineSeparator());
    }

    public static String getHeadId() {
        return getBranchHeadsMap().get(getCurrentBranch());
    }

    public static String getCurrentBranch() {
        return readContentsAsString(CURRENTBRANCHFILE);
    }

    public static TreeMap<File, String> getAddMap() {
        return readObject(ADDMAPFILE, TreeMap.class);
    }

    public static TreeMap<String, String> getBranchHeadsMap() {
        return readObject(BRANCHMAPFILE, TreeMap.class);
    }

    public static TreeSet<File> getRmSet() {
        return readObject(RMSETFILE, TreeSet.class);
    }

//    /** Returns the file with the given name in the CWD
//         * Returns null if no such file
//         * Doesn't look inside folders */
//        public static File getFile(String name) {
//            File f = join(CWD, name);
//            if (f.exists()) {
//                return f;
//            }
//            return null;
//    }

    public static void addFile(String filename) {
        // Saves snapshot of contents to Blob directory
        File f = join(CWD, filename);
        if (!f.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        String contentId = sha1(readContents(f));
        File destination = join(BLOBS, contentId);
        String currentBlob = Commit.fromFile(getHeadId()).getFileBlobMap().get(f);
        //Does not create blob/update addMap if no changes were made
        //Two different files with same content will now point to the same blob file
        //Might cause problems
        if (currentBlob == null || !currentBlob.equals(contentId)) {
            if (!destination.exists()) {
                try {
                    Files.copy(f.toPath(), destination.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Updates add map
            TreeMap<File, String> am = getAddMap();
            am.put(f, contentId);
            writeObject(ADDMAPFILE, am);
        }
        // Updates rm map
        TreeSet<File> rs = getRmSet();
        rs.remove(f);
        writeObject(RMSETFILE, rs);
    }

    public static void commit(String msg) {
        TreeMap<File, String> am = getAddMap();
        TreeSet<File> rs = getRmSet();
        Commit c = new Commit(msg, am, rs);
        c.toFile();
        //Clear stages
        writeObject(ADDMAPFILE, new TreeMap<File, String>());
        writeObject(RMSETFILE, new TreeSet<File>());
        //Update head
        TreeMap<String, String> branches = getBranchHeadsMap();
        branches.put(getCurrentBranch(), c.getId());
        writeObject(BRANCHMAPFILE, branches);
    }

    public static void printCommit(Commit c) {
        message("===");
        message("commit " + c.getId());
        if (c.getParent2Id() != null) {
            message("Merge: " + c.getParentId().substring(0, 7) + " "
                    + c.getParent2Id().substring(0, 7));
        }
        message("Date: " + SDF.format(c.getTime()));
        message(c.getMessage());
        System.out.println();
    }

    public static void log(String id) {
        Commit c = Commit.fromFile(id);
        printCommit(c);
        String parentId = c.getParentId();
        if (parentId != null) {
            log(parentId);
        }
    }

    public static void gLog() {
        for (String id : plainFilenamesIn(Commit.COMMITS)) {
            Commit c = Commit.fromFile(id);
            printCommit(c);
        }
    }

    public static void status() {
        message("=== Branches ===");
        for (String b : getBranchHeadsMap().keySet()) {
            if (b.equals(getCurrentBranch())) {
                System.out.print("*");
            }
            System.out.println(b);
        }
        System.out.println();
        message("=== Staged Files ===");
        for (File f : getAddMap().keySet()) {
            message(f.toPath().getFileName().toString());
        }
        System.out.println();
        message("=== Removed Files ===");
        for (File f : getRmSet()) {
            message(f.toPath().getFileName().toString());
        }
        System.out.println();
        message("=== Modifications Not Staged For Commit ===");
//        Commit head = Commit.fromFile(getHeadId());
//        for (File f : getAddMap().keySet()) {
//            if (!f.exists()) {
//                message(f.toPath().getFileName().toString() + " (deleted)");
//            } else if (!sha1(readContents(f)).equals(getAddMap().get(f))) {
//                message(f.toPath().getFileName().toString() + " (modified)");
//            }
//        }
//        for (File f : head.getFileBlobMap().keySet()) {
//            if (f.exists() && !getAddMap().containsKey(f)
//                    && !sha1(readContents(f)).equals(head.getFileBlobMap().get(f))) {
//                message(f.toPath().getFileName().toString() + " (modified)");
//            } else if (!getRmSet().contains(f) && !f.exists()) {
//                message(f.toPath().getFileName().toString() + " (deleted)");
//            }
//        }
        System.out.println();
        message("=== Untracked Files ===");
//        for (String filename : plainFilenamesIn(CWD)) {
//            File f = join(CWD, filename);
//            if (!head.getFileBlobMap().containsKey(f) && !getAddMap().containsKey(f)) {
//                message(filename);
//            }
//        }
        System.out.println();
    }

    public static void find(String msg) {
        boolean found = false;
        for (String id : plainFilenamesIn(Commit.COMMITS)) {
            String cmsg = Commit.fromFile(id).getMessage();
            if (cmsg.equals(msg)) {
                message(id);
                found = true;
            }
        }
        if (!found) {
            message("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void createBranch(String name) {
        TreeMap<String, String> branches = getBranchHeadsMap();
        if (branches.containsKey(name)) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        branches.put(name, getHeadId());
        writeObject(BRANCHMAPFILE, branches);
    }

    public static void updateCWD(TreeMap<File, String> fbm, File f) {
        // Case: not tracked in current commit but in CWD
        // REAAALLY should check this for all files before changing the CWD
        if (f.exists() && !Commit.fromFile(getHeadId()).getFileBlobMap().containsKey(f)) {
            message("There is an untracked file in the way; delete it, "
                    + "or add and commit it first.");
            System.exit(0);
        }
        if (!f.exists()) {
            //Doesn't work for any file in a subdirectory of the CWD
            makeFile(f);
        }
        if (!fbm.containsKey(f)) {
            message("File does not exist in that commit.");
            System.exit(0);
        } else {
            File blob = join(BLOBS, fbm.get(f));
            try {
                Files.copy(blob.toPath(), f.toPath(), REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Updates all files in the fbm */
    public static void updateCWD(TreeMap<File, String> fbm) {
        for (File f : fbm.keySet()) {
            TreeMap<File, String> currentfbm = Commit.fromFile(getHeadId()).getFileBlobMap();
//            boolean untracked = !currentfbm.get(f).equals(sha1(readContents(f)));
            boolean untracked1 = currentfbm.containsKey(f)
                    && !currentfbm.get(f).equals(sha1(readContents(f)));
            boolean untracked2 = !currentfbm.containsKey(f) && !getAddMap().containsKey(f);
            if (f.exists() && (untracked1 || untracked2)) {
                message("There is an untracked file in the way; delete it, "
                        + "or add and commit it first.");
                System.exit(0);
            }
        }
        for (File f :fbm.keySet()) {
            if (!f.exists()) {
                //Doesn't work for any file in a subdirectory of the CWD
                makeFile(f);
            }
            if (!fbm.containsKey(f)) {
                message("File does not exist in that commit.");
                System.exit(0);
            } else {
                File blob = join(BLOBS, fbm.get(f));
                try {
                    Files.copy(blob.toPath(), f.toPath(), REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void checkout(String id, String filename) {
        TreeMap<File, String> fbm = Commit.fromFile(id).getFileBlobMap();
        File f = join(CWD, filename);
        updateCWD(fbm, f);
    }

    public static void checkoutBranch(String branch) {
        // Check for files that could be overwritten before changing anything?
        if (branch.equals(getCurrentBranch())) {
            message("No need to checkout the current branch.");
            System.exit(0);
        }
        String branchHeadId = getBranchHeadsMap().get(branch);
        if (branchHeadId == null) {
            message("No such branch exists.");
            System.exit(0);
        }
        TreeMap<File, String> fbm = Commit.fromFile(branchHeadId).getFileBlobMap();
        // Write a "parent" method that checks every file before updating?
        for (File f : fbm.keySet()) {
            updateCWD(fbm, f);
        }
        // Delete all files in current commit but not in checked out branch
        for (File f : Commit.fromFile(getHeadId()).getFileBlobMap().keySet()) {
            if (!fbm.containsKey(f)) {
                restrictedDelete(f);
            }
        }
        // Clear stages
        writeObject(ADDMAPFILE, new TreeMap<File, String>());
        writeObject(RMSETFILE, new TreeSet<File>());
        // Update current branch
        writeContents(CURRENTBRANCHFILE, branch);
    }

    public static void remove(String filename) {
        // get head commit
        Commit head = Commit.fromFile(getHeadId());
        // get maps
        TreeMap<File, String> addmap = getAddMap();
        // get corresponding file
        File f = join(CWD, filename);
        if (addmap.containsKey(f)) {
            addmap.remove(f);
            writeObject(ADDMAPFILE, addmap);
        } else if (head.getFileBlobMap() != null && head.getFileBlobMap().containsKey(f)) {
            TreeSet<File> rmSet = getRmSet();
            rmSet.add(f);
            writeObject(RMSETFILE, rmSet);
            restrictedDelete(f);
        } else {
            message("No reason to remove the file.");
            System.exit(0);
        }
    }

    public static void removeBranch(String branchName) {
        if (branchName.equals(getCurrentBranch())) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        TreeMap<String, String> branches = getBranchHeadsMap();
        if (!branches.containsKey(branchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        } else {
            branches.remove(branchName);
            writeObject(BRANCHMAPFILE, branches);
        }
    }

    public static void reset(String id) {
        TreeMap<File, String> fbm = Commit.fromFile(id).getFileBlobMap();
        updateCWD(fbm);
        // Check for untracked files before doing anything?
        // Remove tracked files not present in commit?
        TreeMap<String, String> branches = getBranchHeadsMap();
        branches.put(getCurrentBranch(), id);
        writeObject(BRANCHMAPFILE, branches);
        // Clear stages
        writeObject(ADDMAPFILE, new TreeMap<File, String>());
        writeObject(RMSETFILE, new TreeSet<File>());
    }

    public static void merge(String otherBranch) {
        if (!getRmSet().isEmpty() || !getAddMap().isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        if (!getBranchHeadsMap().containsKey(otherBranch)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (otherBranch.equals(getCurrentBranch())) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        String currentHead = getHeadId();
        String otherHead = getBranchHeadsMap().get(otherBranch);
        String splitId = findSplitId(otherBranch);
        if (splitId.equals(otherHead)) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitId.equals(currentHead)) {
            message("Current branch fast-forwarded.");
            checkoutBranch(otherBranch);
            System.exit(0);
        }
        TreeMap<File, String> splitFiles = Commit.fromFile(splitId).getFileBlobMap();
        TreeMap<File, String> currentFiles = Commit.fromFile(currentHead).getFileBlobMap();
        TreeMap<File, String> otherFiles = Commit.fromFile(otherHead).getFileBlobMap();
        TreeSet<File> pooled = new TreeSet<>(splitFiles.keySet());
        pooled.addAll(currentFiles.keySet());
        pooled.addAll(otherFiles.keySet());
        TreeSet<File> rmSet = getRmSet();
        TreeMap<File, String> addMap = getAddMap();
        boolean conflict = false;
        for (File f : pooled) {
            String splitBlob = splitFiles.get(f);
            String currentBlob = currentFiles.get(f);
            String otherBlob = otherFiles.get(f);
            // Check for untracked files in current commit before changing anything?
            if (safeEquals(currentBlob, splitBlob)) {
                if (otherBlob != null) {
                    //Same/missing in split and current, modified in other
                    // May have to write own method instead of updateCWD?
                    updateCWD(otherFiles, f);
                    addMap.put(f, otherFiles.get(f));
                } else {
                    //Same in split and current, missing in other
                    restrictedDelete(f);
                    rmSet.add(f);
                }
            } else if (!safeEquals(currentBlob, otherBlob) && !safeEquals(splitBlob, otherBlob)) {
                //Merge conflict (modified in other and modified in current differently)
                conflict = true;
                String otherString = "";
                String currentString = "";
                if (otherBlob != null) {
                    otherString = readContentsAsString(join(BLOBS, otherBlob));
                }
                if (currentBlob != null) {
                    currentString = readContentsAsString(join(BLOBS, currentBlob));
                }
                writeContents(f, "<<<<<<< HEAD" + System.lineSeparator() + currentString
                        + "=======" + System.lineSeparator() + otherString
                        + ">>>>>>>" + System.lineSeparator());
            }
        }
        Commit c = new Commit("Merged " + otherBranch + " into "
                + getCurrentBranch() + ".", addMap, rmSet);
        c.setParent2Id(otherHead);
        c.toFile();
        writeObject(RMSETFILE, new TreeSet<File>());
        writeObject(ADDMAPFILE, new TreeMap<File, String>());
        TreeMap<String, String> branches = getBranchHeadsMap();
        branches.put(getCurrentBranch(), c.getId());
        writeObject(BRANCHMAPFILE, branches);
        if (conflict) {
            message("Encountered a merge conflict.");
        }
    }

    private static String findSplitId(String otherBranch) {
//        message("Finding split point...");
        String currentHead = getHeadId();
        String otherHead = getBranchHeadsMap().get(otherBranch);
        LinkedList<String> fringeCurrent = new LinkedList<>();
        LinkedList<String> fringeOther = new LinkedList<>();
        fringeCurrent.add(currentHead);
        fringeOther.add(otherHead);
        TreeSet<String> seenCurrent = new TreeSet<>();
        TreeSet<String> seenOther = new TreeSet<>();
        String splitId = null;
        while (splitId == null) {
            if (!fringeCurrent.isEmpty()) {
//                message("Exploring fringe of currentHead...");
                String temp1 = fringeCurrent.removeFirst();
//                message("Looking at " + temp1);
                if (seenOther.contains(temp1)) {
//                    message(temp1 + " is the split point!");
                    splitId = temp1;
                    break;
                } else {
//                    message(temp1 + " is not the split point, added to seenCurrent");
                    seenCurrent.add(temp1);
                }
                Commit c1 = Commit.fromFile(temp1);
                if (c1.getParentId() != null) {
//                    message("Adding parent 1 " + c1.getParentId() + " to fringeCurrent.");
                    fringeCurrent.addLast(c1.getParentId());
                }
                if (c1.getParent2Id() != null) {
//                    message("Adding parent 2 " + c1.getParent2Id() + " to fringeCurrent.");
                    fringeCurrent.addLast(c1.getParent2Id());
                }
            }
            if (!fringeOther.isEmpty()) {
//                message("Exploring fringe of otherHead...");
                String temp2 = fringeOther.removeFirst();
//                message("Looking at " + temp2);
                if (seenCurrent.contains(temp2)) {
//                    message(temp2 + " is the split point!");
                    splitId = temp2;
                    break;
                } else {
//                    message(temp2 + " is not the split point, added to seenOther");
                    seenOther.add(temp2);
                }
                Commit c2 = Commit.fromFile(temp2);
                if (c2.getParentId() != null) {
//                    message("Adding parent 1 " + c2.getParentId() + " to fringeOther.");
                    fringeOther.addLast(c2.getParentId());
                }
                if (c2.getParent2Id() != null) {
//                    message("Adding parent 2 " + c2.getParent2Id() + " to fringeOther.");
                    fringeOther.addLast(c2.getParent2Id());
                }
            }
        }
        return splitId;
    }
}
