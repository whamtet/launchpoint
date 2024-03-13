exports.typeInput = (page, name, text) => page.type(`input[name="${name}"]`, text);
exports.clickValue = (page, name) => page.click(`input[value="${name}"]`);
