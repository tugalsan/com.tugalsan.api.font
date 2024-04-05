module com.tugalsan.api.font {
    requires java.desktop;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.thread;
    requires com.tugalsan.api.tuple;
    requires com.tugalsan.api.union;
    requires com.tugalsan.api.stream;
    requires com.tugalsan.api.callable;
    requires com.tugalsan.api.coronator;
    exports com.tugalsan.api.font.client;
    exports com.tugalsan.api.font.server;
}
