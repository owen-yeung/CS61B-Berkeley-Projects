package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet commit object.
 *
 *  @author Owen
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The commits directory. */
    public static final File COMMITS = join(Repository.GITLET_DIR, "commits");
//    public static final String initId = new Commit().getId();
    /** The message of this Commit. */
    private String message;
    /** The time of this Commit. */
    private Date time;
    /** The id of the parent of this Commit. */
    private String parentId;
    private String parent2Id;
    /** A mapping between files in the CWD and the blob ids of their snapshot in this commit */
    private TreeMap<File, String> fileBlobMap;

    public Commit(String msg, TreeMap<File, String> addMap, TreeSet<File> rmSet) {
        if (msg.trim().equals("")) {
            message("Please enter a commit message.");
            System.exit(0);
        } else if (addMap.isEmpty() && rmSet.isEmpty()) {
            message("No changes added to the commit.");
        }
        message = msg;
        time = new Date();
        parentId = Repository.getHeadId();
        parent2Id = null;
        TreeMap<File, String> parentMap = fromFile(parentId).getFileBlobMap();
        if (parentMap == null) {
            fileBlobMap = addMap;
        } else {
            for (File f : rmSet) {
                parentMap.remove(f);
            }
            parentMap.putAll(addMap);
            fileBlobMap = parentMap;
        }
    }

    /** Constructs the initial commit. */
    public Commit() {
        message = "initial commit";
        time = new Date(0);
        parentId = null;
        parent2Id = null;
        fileBlobMap = new TreeMap<File, String>();
    }

    public String getParentId() {
        return parentId;
    }

    public String getParent2Id() {
        return parent2Id;
    }

    public void setParent2Id(String id) {
        parent2Id = id;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return sha1(serialize(this));
    }

    public TreeMap<File, String> getFileBlobMap() {
        return fileBlobMap;
    }

//    public String getTimeString() {
//        return timeString;
//    }

    public void toFile() {
        File newFile = join(COMMITS, getId());
        if (!newFile.isFile()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeObject(newFile, this);
    }

    /** Gets the Commit Object from its file in COMMITS
     * Exists with error message if no commit with the given id exists in COMMITS */
    public static Commit fromFile(String id) {
        if (id.length() < UID_LENGTH) {
            for (String i : plainFilenamesIn(COMMITS)) {
                if (i.startsWith(id)) {
                    id = i;
                    break;
                }
            }
        }
        File cFile = join(COMMITS, id);
        if (id.length() == 40 && cFile.exists()) {
            Commit c = readObject(cFile, Commit.class);
            return c;
        } else {
            message("No commit with that id exists.");
            System.exit(0);
        }
        return null;
    }
}
