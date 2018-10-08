package nl.bravobit.ffmpeg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

class ShellCommand {

    private class PipeWriter implements Runnable
    {
        final InputStream in;
        final OutputStream out;

        PipeWriter(InputStream in, OutputStream out)
        {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run()
        {
            try
            {
                byte [] buffer = new byte[10*1024];
                int length;
                while ( (length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
            }
            catch(IOException e)
            {
                Log.e("Exception while trying to write to ffmpeg stdin", e);
            }
            finally
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    Log.e("Failed to close stream", e);
                }
            }
        }
    }

    Process run(String[] commandString, final InputStream pipeToStdin) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(commandString);
            if(pipeToStdin != null)
            {
                new Thread(new PipeWriter(pipeToStdin, process.getOutputStream())).start();
            }

        } catch (IOException e) {
            Log.e("Exception while trying to run: " + Arrays.toString(commandString), e);
        }
        return process;
    }

}