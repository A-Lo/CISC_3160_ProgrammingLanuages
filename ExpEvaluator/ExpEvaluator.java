import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Scanner;

public class ExpEvaluator {

    private String s;
    private int currIndex;
    private char inputToken;
    private HashMap<String, Integer> hash_map = new HashMap<String, Integer>();
    //HashMap (key, value )

    void expEvaluator(String s) {
        this.s = s.replaceAll("\\s", ""); //Removes whitespaces in a string
        currIndex = 0;
        nextToken();
    }
    //Checks whether the string end with semicolon
    //Gets the next element of the string input
    void nextToken() {
        char c;
        if (!s.endsWith(";"))
            throw new RuntimeException("Missing ';' token exptected (1)");
        c = s.charAt(currIndex++);

        inputToken = c;
    }

    //Determines if there is a closing paranthesis
    void match(char token) {
        if (inputToken == token) {
            nextToken();
        } else {
            throw new RuntimeException("Missing Parenthesis");
        }
    }

    //Determines the the assignment end with semicolon
    int eval() {
        int x = exp();
        if (inputToken == ';') {
            return x;
        } else {
            throw new RuntimeException("Missing ';' token expected (2)");
        }
    }

    //The call from main(); starts reading the file, line-by-line, using loop.
    public void run(Scanner fileText) {
        //Continuously checks if the input scanner has another line to be read
        while (fileText.hasNextLine()) {
            expEvaluator(fileText.nextLine());
            declareVar();
        }
    }

    //Will be initializing the variable and the value assigned to the variable
    void declareVar() {

        String var = identifier(); //gets the sb.toString(), the variable
        int operand = eval(); //gets the value or the operand
        hash_map.put(var, operand); //Stores the var and operand into hashmap
        System.out.println("Output:");
        System.out.println(var + " = " + operand);

    }
    //If the operator is addition or subtraction
    int exp() {
        int x = term();
        while (inputToken == '+' || inputToken == '-') {
            char op = inputToken;
            nextToken();
            int y = term();
            x = apply(op, x, y);
        }
        return x;
    }
    //If the operator is Multiplication or Division
    int term() {
        int x = factor();
        while (inputToken == '*' || inputToken == '/') {
            char op = inputToken;
            nextToken();
            int y = factor();
            x = apply(op, x, y);
        }
        return x;
    }

    //Function will run if there is more than one line
    //More than one assignment declarations
    int factor() {
        int x = 0;
        String temp = String.valueOf(inputToken);

        if (hash_map.containsKey(temp)) {
            x = hash_map.get(temp).intValue();
            nextToken();
            return x;
        } else if (inputToken == '(') {
            nextToken();
            x = exp();
            match(')');
            return x;
        } else if (inputToken == '-') {
            nextToken();
            x = factor();
            return -x;
        } else if (inputToken == '+') {
            nextToken();
            x = factor();
            return x;
        } else if (inputToken == '0') {
            nextToken();
            if (Character.isDigit(inputToken))
                throw new RuntimeException("Invalid value");
            return 0;
        }
        temp = "";

        while (Character.isDigit(inputToken)) {
            temp += inputToken;
            nextToken();
        }

        return Integer.parseInt(temp);

    }

//Identifies the initialization of a variable, returns the variable value
    String identifier() {
        StringBuilder sb = new StringBuilder();
        
        if (Character.isLetter(inputToken))
            sb.append(inputToken);
        else
            throw new RuntimeException("Invalid variable name");
        nextToken(); //Gets the next token that follows variable, should be (=)

        while (Character.isLetter(inputToken) || inputToken == '_' || Character.isDigit(inputToken)) {
            sb.append(inputToken);
            nextToken();
        }
        if (inputToken != '=')
            throw new RuntimeException("Not an valid assignment statement");
        nextToken(); // Moves the token over to the operand; after the (=)
        return sb.toString(); //Returns the first variable, to string 'var'
    }

    //Operation to compute
    static int apply(char op, int x, int y) {
        int z = 0;
        switch (op) {
            case '+':
                z = x + y;
                break;
            case '-':
                z = x - y;
                break;
            case '*':
                z = x * y;
                break;
            case '/':
                z = x / y;
                break;
        }
        return z;
    }

    public static void main(String[] args) {

        try {
            //Creates a scanner object; takes the file user enters in command line and stores into the scanner-object
            Scanner fileObj = new Scanner(new FileInputStream(args[0]));
            
            //Creates object for ExpEvaluator class
            ExpEvaluator expEval = new ExpEvaluator();
            expEval.run(fileObj);
 
        } // If the file isn't passed as parameter by user, or file doesn't exist
        catch (Exception exp) {
            System.out.println("File doesn't exist: " + exp);
        }
    }
}
