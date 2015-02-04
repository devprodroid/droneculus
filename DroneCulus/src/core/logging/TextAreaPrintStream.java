package core.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * PrintStream for logging: System Console is unreadable, because of YaDrone CommandManager
 *
 */
public class TextAreaPrintStream extends PrintStream {
    private final OutputStream second;

    public TextAreaPrintStream(OutputStream main, OutputStream second) {
        super(main);
        this.second = second;
    }

    /**
     * Closes the main stream. 
     * The second stream is just flushed but <b>not</b> closed.
     * @see java.io.PrintStream#close()
     */
    @Override
    public void close() {
        // just for documentation
        super.close();
    }

    @Override
    public void flush() {
        super.flush();
        try {
			second.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        try {
			second.write(buf, off, len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void write(int b) {
        super.write(b);
        try {
			second.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        second.write(b);
    }
}