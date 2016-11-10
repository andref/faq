package faq;

import com.codahale.metrics.JmxReporter;
import io.dropwizard.lifecycle.Managed;

import java.util.Objects;

class ManagedJmxReporter implements Managed {

    private final JmxReporter reporter;

    ManagedJmxReporter(JmxReporter reporter) {
        this.reporter = Objects.requireNonNull(reporter);
    }

    @Override
    public void start() throws Exception {
        reporter.start();
    }

    @Override
    public void stop() throws Exception {
        reporter.stop();
    }
}
