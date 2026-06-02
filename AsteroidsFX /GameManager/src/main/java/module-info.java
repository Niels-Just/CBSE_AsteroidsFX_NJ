import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

module GameManager {
    requires Common;
    requires spring.web;
    provides IPostEntityProcessingService with dk.sdu.mmmi.cbse.gamemanager.GameManagerService;
}
