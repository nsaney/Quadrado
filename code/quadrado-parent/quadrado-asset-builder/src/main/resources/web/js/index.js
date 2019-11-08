$(function () {
    var self = {};
    ko.applyBindings(self);
    window.backend.getLogger().info('Knockout Bindings applied!');
});
