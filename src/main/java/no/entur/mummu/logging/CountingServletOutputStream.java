package no.entur.mummu.logging;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.IOException;

/**
 * Delegating ServletOutputStream that counts bytes written and fires a one-shot
 * callback the first time the cumulative count crosses a threshold. Does not
 * buffer. Not thread-safe: a single response is written by a single thread.
 */
class CountingServletOutputStream extends ServletOutputStream {

    private final ServletOutputStream delegate;
    private final long threshold;
    private final Runnable onThresholdCrossed;
    private long count;
    private boolean crossed;

    CountingServletOutputStream(ServletOutputStream delegate, long threshold, Runnable onThresholdCrossed) {
        this.delegate = delegate;
        this.threshold = threshold;
        this.onThresholdCrossed = onThresholdCrossed;
    }

    long count() {
        return count;
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
        add(1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
        add(len);
    }

    private void add(long n) {
        count += n;
        if (!crossed && count > threshold) {
            crossed = true;
            onThresholdCrossed.run();
        }
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        delegate.setWriteListener(writeListener);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
