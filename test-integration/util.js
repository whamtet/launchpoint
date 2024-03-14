exports.typeInput = (page, name, text) => page.type(`input[name="${name}"]`, text);
exports.typeTextArea = (page, name, text) => page.type(`textarea[name="${name}"]`, text);
exports.clickInput = (page, name) => page.click(`input[value="${name}"]`);
exports.clickButton = (page, text) => page.click(`xpath/.//button[contains(text(), '${text}')]`);
exports.setInput = (page, name, text) => page.$eval(`input[name="${name}"]`, (input, text) => input.value = text, text);
