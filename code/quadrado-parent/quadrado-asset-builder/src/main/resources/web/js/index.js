$(function () {
    var self = {};
    self.name = backend.getName();
    self.nanoTime = backend.getNanoTime();
    ko.applyBindings(self);
});
