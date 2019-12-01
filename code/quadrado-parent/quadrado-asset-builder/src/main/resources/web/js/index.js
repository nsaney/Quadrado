$(function () {

    ////// Util Classes //////
    var TabbedWindow_nextId = 0;
    function TabbedWindow(_name) {
        var self = this;
        self.id = ++TabbedWindow_nextId;
        self.name = _name;
        var htmlTabId = 'tab-' + self.id;
        var htmlPanelId = 'panel-' + self.id;
        self.tabAttrs = {
            id: htmlTabId,
            href: '#' + htmlPanelId,
            ariaControls: htmlPanelId
        };
        self.panelAttrs = {
            id: htmlPanelId,
            ariaLabelledby: htmlTabId
        };
    }


    ////// Root //////
    var root = {};
    root.tabbedWindows = [
        new TabbedWindow('Font'),
        new TabbedWindow('BoxStyle'),
        new TabbedWindow('MapRoom'),
        new TabbedWindow('Sprite'),
        new TabbedWindow('TileSet')
    ];


    ////// Init //////
    ko.applyBindings(root);
    backend.logger.info('Knockout Bindings applied!');
});
