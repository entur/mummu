package no.entur.mummu.logging;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountingServletOutputStreamTest {

    private static ServletOutputStream sink(ByteArrayOutputStream buf) {
        return new ServletOutputStream() {
            @Override public void write(int b) { buf.write(b); }
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(WriteListener writeListener) {
                // no-op: synchronous test sink, async write callbacks are not exercised
            }
        };
    }

    @Test
    void countsBytesAndFiresCallbackOnceOnCrossing() throws Exception {
        var buf = new ByteArrayOutputStream();
        var callbacks = new AtomicInteger();
        var out = new CountingServletOutputStream(sink(buf), 10, callbacks::incrementAndGet);

        out.write(new byte[8], 0, 8);
        assertEquals(0, callbacks.get(), "not crossed yet");

        out.write(new byte[8], 0, 8); // total 16 > 10
        assertEquals(1, callbacks.get(), "fires on crossing");

        out.write(new byte[8], 0, 8); // total 24, must not fire again
        assertEquals(1, callbacks.get(), "fires at most once");

        assertEquals(24, out.count());
        assertEquals(24, buf.size(), "all bytes passed through to delegate");
    }
}
